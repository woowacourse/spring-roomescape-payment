package roomescape.reservation.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.application.service.PaymentService;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;
import roomescape.reservation.domain.repository.WaitingRepository;
import roomescape.reservation.presentation.dto.AdminReservationRequest;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;
import roomescape.reservation.presentation.dto.UserReservationsResponse;

@Service
public class ReservationService {

    private final PaymentService paymentService;

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(final PaymentService paymentService, final WaitingRepository waitingRepository,
                              final ReservationRepository reservationRepository,
                              final ReservationTimeRepository reservationTimeRepository,
                              final ThemeRepository themeRepository,
                              final MemberRepository memberRepository) {
        this.paymentService = paymentService;
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ReservationResponse createUserReservationAndPayment(final ReservationRequest reservationRequest, final Long memberId) {
        Member member = findMemberById(memberId);
        Reservation unpaidReservation = makeUnpaidReservation(reservationRequest.getTimeId(), reservationRequest.getThemeId(), reservationRequest.getDate(), member);
        unpaidReservation.validateIsPast();

        Payment payment = paymentService.processPaymentRequest(reservationRequest);

        return createPaidReservation(unpaidReservation, member, payment);
    }

    private ReservationResponse createPaidReservation(final Reservation reservation, final Member member, final Payment payment) {
        final Reservation paidReservation = new Reservation(
                member,
                reservation.getTheme(),
                reservation.getDate(),
                reservation.getReservationTime(),
                payment
        );

        return createReservation(paidReservation);
    }

    @Transactional
    public ReservationResponse createAdminReservation(final AdminReservationRequest adminReservationRequest) {
        Member member = findMemberById(adminReservationRequest.getMemberId());

        Reservation unpaidReservation = makeUnpaidReservation(
                adminReservationRequest.getTimeId(),
                adminReservationRequest.getThemeId(),
                adminReservationRequest.getDate(),
                member
        );

        return createReservation(unpaidReservation);
    }

    private Reservation makeUnpaidReservation(final Long timeId, final Long themeId, final LocalDate date, final Member member) {
        ReservationTime reservationTime = getReservationTime(timeId);
        Theme theme = getTheme(themeId);
        validateIsDuplicate(date, reservationTime);

        return new Reservation(member, theme, date, reservationTime, null);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(final Long memberId, final Long themeId, final LocalDate dateFrom,
                                                     final LocalDate dateTo) {

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("dateFrom은 dateTo보다 이전이어야 합니다.");
        }

        return reservationRepository.findAllByMemberIdAndThemeIdAndDateBetween(memberId, themeId, dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserReservationsResponse> getUserReservations(final Long memberId) {
        findMemberById(memberId);

        final List<UserReservationsResponse> reservations = reservationRepository.findByMemberId(memberId).stream()
                .map(UserReservationsResponse::new)
                .toList();

        final List<UserReservationsResponse> waitings = waitingRepository.findWaitingWithRankByMemberId(
                        memberId).stream()
                .map(UserReservationsResponse::new)
                .toList();

        return Stream.concat(
                reservations.stream(),
                waitings.stream()
        ).toList();
    }

    @Transactional
    public void deleteReservation(final Long id) {

        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("이미 삭제되어 있는 리소스입니다."));

        reservationRepository.delete(reservation);

        final Optional<Waiting> waiting = waitingRepository.findFirstByReservationInfoOrderByIdAsc(
                reservation.getReservationInfo()
        );

        waiting.ifPresent(value -> {
            reservationRepository.save(new Reservation(
                    value.getMember(),
                    value.getReservationInfo()
            ));

            waitingRepository.delete(waiting.get());
        });
    }

    private ReservationResponse createReservation(final Reservation reservation) {
        return new ReservationResponse(reservationRepository.save(reservation));
    }

    private ReservationTime getReservationTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("예약 시간 정보를 찾을 수 없습니다."));
    }

    private Theme getTheme(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("테마 정보를 찾을 수 없습니다."));
    }

    private void validateIsDuplicate(final LocalDate reservationDate, final ReservationTime reservationTime) {
        if (reservationRepository.existsByReservationInfoDateAndReservationInfoReservationTimeStartAt(reservationDate,
                reservationTime.getStartAt())) {
            throw new IllegalStateException("중복된 일시의 예약은 불가능합니다.");
        }
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("유저 정보를 찾을 수 없습니다."));
    }
}
