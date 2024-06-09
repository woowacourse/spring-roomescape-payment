package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.TossPaymentCancelResponse;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waitings;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationCancelReason;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationSaveRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            PaymentService paymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentService = paymentService;
    }

    public ReservationResponse save(
            UserReservationSaveRequest userReservationSaveRequest,
            LoginMember loginMember,
            ReservationStatus status
    ) {
        ReservationTime reservationTime = reservationTimeRepository.fetchById(userReservationSaveRequest.timeId());
        Theme theme = themeRepository.fetchById(userReservationSaveRequest.themeId());
        Member member = memberRepository.fetchById(loginMember.id());
        Reservation reservation = userReservationSaveRequest.toEntity(member, theme, reservationTime, status);
        validateReservation(loginMember, reservation);

        Reservation savedReservation = reservationRepository.save(reservation);
        if (status.isSuccess()) {
            paymentService.payForReservation(userReservationSaveRequest.extractPaymentRequest(), savedReservation);
        }

        return ReservationResponse.toResponse(savedReservation);
    }

    private void validateReservation(LoginMember loginMember, Reservation reservation) {
        if (reservation.getStatus().isSuccess()) {
            if (reservationRepository.existsByThemeAndDateAndTimeStartAtAndStatus(
                    reservation.getTheme(),
                    reservation.getDate(),
                    reservation.getStartAt(),
                    reservation.getStatus()
            )) {
                throw new IllegalArgumentException("중복된 예약이 있습니다.");
            }
        }

        if (reservation.getStatus().isWait()) {
            List<ReservationStatus> reservationStatuses = reservationRepository.findStatusesByMemberIdAndThemeAndDateAndTimeStartAt(
                    loginMember.id(),
                    reservation.getTheme(),
                    reservation.getDate(),
                    reservation.getStartAt()
            );
            if (reservationStatuses.contains(ReservationStatus.SUCCESS) || reservationStatuses.contains(ReservationStatus.WAIT)) {
                throw new IllegalArgumentException("예약이 완료되었거나, 대기 상태로 등록된 예약입니다.");
            }
        }
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.fetchById(id);

        return ReservationResponse.toResponse(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByStatus(ReservationStatus reservationStatus) {
        List<Reservation> waitingReservations = reservationRepository.findAllByStatusFromDate(reservationStatus, LocalDate.now());

        return waitingReservations.stream()
                .map(ReservationResponse::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> findMemberReservations(LoginMember loginMember) {
        List<Reservation> waitingReservations = reservationRepository.findAllByStatusFromDate(ReservationStatus.WAIT, LocalDate.now());

        Waitings waitings = new Waitings(waitingReservations);

        return reservationRepository.findAllMemberReservationWithPayment(loginMember.id(), LocalDate.now()).stream()
                .map(reservation -> MemberReservationResponse.toResponse(reservation, waitings.findMemberRank(reservation, loginMember.id())))
                .toList();
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    public TossPaymentCancelResponse cancelById(Long id, ReservationCancelReason reservationCancelReason) {
        Reservation canceledReservation = getCanceledReservation(id);
        updateFirstWaitingReservation(canceledReservation);
        return paymentService.cancel(id, reservationCancelReason);
    }

    private Reservation getCanceledReservation(Long id) {
        Reservation successReservation = reservationRepository.fetchById(id);

        if (successReservation.getDate().equals(LocalDate.now())) {
            throw new IllegalArgumentException("당일 예약은 취소할 수 없습니다.");
        }
        successReservation.updateStatus(ReservationStatus.CANCEL);

        return successReservation;
    }

    private void updateFirstWaitingReservation(Reservation canceledReservation) {
        reservationRepository.findFirstWaitingReservationBy(
                canceledReservation.getDate(),
                canceledReservation.getTime().getId(),
                canceledReservation.getTheme().getId()
        ).ifPresent(reservation -> reservation.updateStatus(ReservationStatus.PAYMENT_PENDING));
    }
}
