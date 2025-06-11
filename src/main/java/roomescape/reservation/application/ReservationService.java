package roomescape.reservation.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.application.ApprovalService;
import roomescape.approval.domain.AdminApproval;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.Payment;
import roomescape.approval.exception.payment.InvalidPaymentAmountException;
import roomescape.approval.exception.payment.PaymentSessionExpiredException;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.reservation.application.dto.AdminReservationRequest;
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
import roomescape.waiting.domain.WaitingRepository;
import roomescape.waiting.domain.WaitingWithRank;
import roomescape.waiting.domain.Waitings;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ApprovalService approvalService;

    public List<MyReservationResponse> findAllByMemberId(Long memberId) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(memberId);
        List<Approval> approvals = approvalService.findAllByReservationIn(reservations);
        List<WaitingWithRank> rankedWaitings = getWaitingWithRanks(memberId);

        return MyReservationResponse.of(reservations, approvals, rankedWaitings);
    }

    private List<WaitingWithRank> getWaitingWithRanks(Long memberId) {
        return waitingRepository.findByMemberId(memberId).stream()
                .map(waiting -> {
                    Waitings waitings = new Waitings(waitingRepository.findBySpec(waiting.getSpec()));
                    long rank = waitings.getRankOf(waiting);
                    return new WaitingWithRank(waiting, rank);
                })
                .toList();
    }

    public List<ReservationResponse> findFiltered(Long memberId, Long themeId, LocalDate from, LocalDate to) {
        return ReservationResponse.from(reservationRepository.findFiltered(memberId, themeId, from, to));
    }

    @Transactional
    public ReservationResponse createByUser(Long memberId, UserReservationRequest request, BigDecimal originAmount) {
        String orderId = request.orderId();
        BigDecimal amount = request.amount();
        validateAmount(originAmount, amount);

        Reservation reservation = reservationRepository.save(
                create(memberId, request.date(), request.timeId(), request.themeId()));
        approvalService.approve(new Payment(reservation, orderId, request.paymentKey(), amount));
        return ReservationResponse.from(reservation);
    }

    private void validateAmount(BigDecimal originAmount, BigDecimal amount) {
        if (originAmount == null) {
            throw new PaymentSessionExpiredException();
        }

        if (originAmount.compareTo(amount) != 0) {
            throw new InvalidPaymentAmountException();
        }
    }

    @Transactional
    public ReservationResponse createByAdmin(AdminReservationRequest request, Long adminId) {
        Member admin = memberRepository.findById(adminId).orElseThrow(MemberNotFoundException::new);
        Reservation reservation = reservationRepository.save(
                create(request.memberId(), request.date(), request.timeId(), request.themeId()));
        approvalService.approve(new AdminApproval(reservation, admin));
        return ReservationResponse.from(reservation);
    }

    private Reservation create(Long memberId, LocalDate dateInput, Long timeId, Long themeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        ReservationDate date = new ReservationDate(dateInput);
        ReservationTime time = timeRepository.findById(timeId).orElseThrow(TimeNotFoundException::new);
        validateInPast(date, time);

        Theme theme = themeRepository.findById(themeId).orElseThrow(ThemeNotFoundException::new);

        ReservationSpec spec = new ReservationSpec(date, time, theme);
        validateDuplicated(spec);

        return new Reservation(member, spec);
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
        reservation.ifPresent(approvalService::deleteByReservation);
        reservationRepository.deleteById(id);
        publishDeleteEvent(reservation);
    }

    private void publishDeleteEvent(Optional<Reservation> reservation) {
        reservation.ifPresent(value -> eventPublisher.publishEvent(new ReservationDeletedEvent(value)));
    }
}
