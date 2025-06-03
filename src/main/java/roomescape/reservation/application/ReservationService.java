package roomescape.reservation.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.payment.application.PaymentApprovalService;
import roomescape.payment.application.PaymentService;
import roomescape.payment.application.dto.PaymentApprovalRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.exception.InvalidPaymentAmountException;
import roomescape.payment.exception.PaymentSessionExpiredException;
import roomescape.reservation.application.dto.AdminReservationRequest;
import roomescape.reservation.application.dto.AdminReservationSearchRequest;
import roomescape.reservation.application.dto.MyReservationResponse;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.application.dto.UserReservationRequest;
import roomescape.reservation.application.event.ReservationDeletedEvent;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationSpec;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.exception.ReservationAlreadyExistsException;
import roomescape.reservation.exception.ReservationInPastException;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.respository.ReservationTimeRepository;
import roomescape.reservationTime.exception.TimeNotFoundException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;
import roomescape.theme.exception.ThemeNotFoundException;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingRepository;
import roomescape.waiting.domain.WaitingWithRank;
import roomescape.waiting.domain.Waitings;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentApprovalService paymentApprovalService;
    private final PaymentService paymentService;

    public List<MyReservationResponse> findAllByMemberId(Long memberId) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        List<Waiting> myWaitings = waitingRepository.findByMemberId(memberId);
        List<WaitingWithRank> rankedWaitings = getWaitingWithRanks(myWaitings);
        Map<Long, Payment> reservationIdToPayment = findPaymentsGroupedByReservationId(memberId);

        return MyReservationResponse.of(reservations, rankedWaitings, reservationIdToPayment);
    }

    private Map<Long, Payment> findPaymentsGroupedByReservationId(Long memberId) {
        List<Payment> myPayments = paymentService.findAllByMemberId(memberId);
        return myPayments.stream()
                .collect(Collectors.toMap(
                        payment -> payment.getReservation().getId(),
                        payment -> payment
                ));
    }

    private List<WaitingWithRank> getWaitingWithRanks(List<Waiting> myWaitings) {
        return myWaitings.stream()
                .map(waiting -> {
                    Waitings waitings = new Waitings(waitingRepository.findBySpec(waiting.getSpec()));
                    long rank = waitings.getRankOf(waiting);
                    return new WaitingWithRank(waiting, rank);
                })
                .toList();
    }

    public List<ReservationResponse> findFiltered(AdminReservationSearchRequest request) {
        Long memberId = request.memberId();
        Long themeId = request.themeId();
        LocalDate from = request.from();
        LocalDate to = request.to();

        return ReservationResponse.from(reservationRepository.findFiltered(memberId, themeId, from, to));
    }

    @Transactional
    public ReservationResponse createByUser(Long memberId, UserReservationRequest request, BigDecimal originAmount) {
        if (originAmount == null) {
            throw new PaymentSessionExpiredException();
        }
        String orderId = request.orderId();
        BigDecimal amount = request.amount();
        if (originAmount.compareTo(amount) != 0) {
            log.warn("결제 금액 불일치 발생! [orderId: {}] expected = {}, actual = {}", orderId, originAmount, amount);
            throw new InvalidPaymentAmountException();
        }

        paymentApprovalService.approvePayment(new PaymentApprovalRequest(orderId, amount, request.paymentKey()));
        Reservation reservation = create(memberId, request.date(), request.timeId(), request.themeId());
        paymentService.save(new Payment(request.paymentKey(), request.amount(), reservation));

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse createByAdmin(AdminReservationRequest request) {
        return ReservationResponse.from(create(request.memberId(), request.date(), request.timeId(), request.themeId()));
    }

    private Reservation create(Long memberId, LocalDate dateInput, Long timeId, Long themeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        ReservationDate date = new ReservationDate(dateInput);
        ReservationTime time = timeRepository.findById(timeId).orElseThrow(TimeNotFoundException::new);
        validateInPast(date, time);

        Theme theme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);

        ReservationSpec spec = new ReservationSpec(date, time, theme);
        validateDuplicated(spec);

        Reservation reservation = new Reservation(member, spec);
        return reservationRepository.save(reservation);
    }

    private void validateDuplicated(ReservationSpec spec) {
        if (reservationRepository.existsBySpec(spec)) {
            throw new ReservationAlreadyExistsException();
        }
    }

    private void validateInPast(ReservationDate date, ReservationTime time) {
        if (date.isInPast() || date.isToday() && time.isBeforeNow()) {
            throw new ReservationInPastException();
        }
    }

    @Transactional
    public void deleteById(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        reservationRepository.deleteById(id);
        publishDeleteEvent(reservation);
    }

    private void publishDeleteEvent(Optional<Reservation> reservation) {
        if (reservation.isPresent()) {
            eventPublisher.publishEvent(new ReservationDeletedEvent(reservation.get()));
        }
    }
}
