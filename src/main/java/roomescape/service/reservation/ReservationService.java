package roomescape.service.reservation;

import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.ALREADY_EXIST_RESERVATION;
import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.NON_EXIST_RESERVATION;
import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.ONLY_PENDING_RESERVATION_CAN_BE_DENIED;
import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.RESERVED_TIME;
import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.WAITING_RESERVATION_REQUIRES_EXISTING;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingReservationResponse;
import roomescape.global.exception.roomescape.RoomEscapeException;
import roomescape.service.member.MemberService;
import roomescape.service.payment.PaymentService;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationItemService reservationItemService;
    private final MemberService memberService;
    private final ReservationThemeService reservationThemeService;
    private final ReservationTimeService reservationTimeService;
    private final PaymentService paymentService;

    public ReservationResponse addReservation(final CreateReservationRequest request) {
        return createReservation(request, ReservationStatus.ACCEPTED, false);
    }

    public ReservationResponse addPendingReservation(final CreateReservationRequest request) {
        return createReservation(request, ReservationStatus.PENDING, true);
    }

    private ReservationResponse createReservation(
            final CreateReservationRequest request,
            final ReservationStatus status,
            final boolean requiresExistingReservation) {

        final Member member = memberService.getMemberById(request.memberId());
        final ReservationTime time = reservationTimeService.getReservationTimeById(request.timeId());
        final ReservationTheme theme = reservationThemeService.getThemeById(request.themeId());
        final LocalDate date = request.date();

        validateReservationAvailability(date, time, theme, requiresExistingReservation);

        final ReservationItem reservationItem = reservationItemService.createReservationItemIfNotExist(
                date, time, theme);

        validateDuplicateReservation(member, reservationItem);

        final Reservation saved = reservationRepository.save(
                Reservation.builder()
                        .member(member)
                        .reservationItem(reservationItem)
                        .reservationStatus(status)
                        .build()
        );
        return ReservationResponse.from(saved);
    }

    private void validateReservationAvailability(
            final LocalDate date,
            final ReservationTime time,
            final ReservationTheme theme,
            final boolean requiresExistingReservation) {

        final boolean reservationExists = reservationItemService.isExistReservationItem(date, time, theme);

        if (requiresExistingReservation && !reservationExists) {
            throw new RoomEscapeException(WAITING_RESERVATION_REQUIRES_EXISTING);
        }

        if (!requiresExistingReservation && reservationExists) {
            throw new RoomEscapeException(RESERVED_TIME);
        }
    }

    private void validateDuplicateReservation(final Member member, final ReservationItem reservationItem) {
        if (reservationRepository.existsByMemberAndReservationItem(member, reservationItem)) {
            throw new RoomEscapeException(ALREADY_EXIST_RESERVATION);
        }
    }

    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllReservations().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> getFilteredReservations(final Long memberId,
                                                             final Long themeId,
                                                             final LocalDate dateFrom,
                                                             final LocalDate dateTo) {
        final List<Reservation> reservations = reservationRepository.findByMemberIdAndThemeIdAndDateFromAndDateTo(
                memberId,
                themeId,
                dateFrom,
                dateTo
        );
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<WaitingReservationResponse> getAllWaitingReservations() {
        List<Reservation> waitingReservations = reservationRepository.findByReservationStatusOrderByIdDesc(ReservationStatus.PENDING);
        return waitingReservations.stream()
                .map(WaitingReservationResponse::from)
                .toList();
    }

    public List<MyPageReservationResponse> getReservationsByMemberId(Long memberId) {
        List<Reservation> reservations = reservationRepository.findByMemberId(memberId);

        final Map<Reservation, Payment> reservationAndPayments = paymentService.getPaymentMapByReservations(reservations);

        return reservations.stream()
                .map(reservation -> {
                    final int priority = calculatePriority(reservation);
                    final Payment payment = reservationAndPayments.get(reservation);

                    return payment != null
                            ? MyPageReservationResponse.from(reservation, priority, payment)
                            : MyPageReservationResponse.from(reservation, priority);
                })
                .toList();
    }

    private int calculatePriority(Reservation reservation) {
        Long reservationItemId = reservation.getReservationItem().getId();
        Long currentReservationId = reservation.getId();

        return (int) reservationRepository.countByReservationItemIdAndIdLessThan(
                reservationItemId, currentReservationId
        );
    }

    @Transactional
    public void denyPendingReservation(Long reservationId) {
        Reservation waitingReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomEscapeException(NON_EXIST_RESERVATION));

        if (waitingReservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new RoomEscapeException(ONLY_PENDING_RESERVATION_CAN_BE_DENIED);
        }

        waitingReservation.changeStatusToDenied();
    }

    @Transactional
    public void removeReservation(Long reservationId) {
        Reservation targetReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomEscapeException(NON_EXIST_RESERVATION));

        if (targetReservation.getReservationStatus() == ReservationStatus.PENDING) {
            deleteReservationOnly(targetReservation);
        } else if (targetReservation.getReservationStatus() == ReservationStatus.ACCEPTED) {
            handleAcceptedReservationRemoval(targetReservation);
        }
    }

    private void handleAcceptedReservationRemoval(Reservation targetReservation) {
        ReservationItem reservationItem = targetReservation.getReservationItem();

        reservationRepository.findFirstByReservationItemAndReservationStatusOrderByIdAsc(
                reservationItem, ReservationStatus.PENDING
        ).ifPresentOrElse(
                nextReservation -> {
                    nextReservation.changeStatusToAccepted();
                    reservationRepository.save(nextReservation);
                    deleteReservationOnly(targetReservation);
                },
                () -> deleteReservationWithItem(targetReservation, reservationItem)
        );
    }

    private void deleteReservationOnly(Reservation reservation) {
        reservationRepository.deleteById(reservation.getId());
    }

    private void deleteReservationWithItem(Reservation reservation, ReservationItem reservationItem) {
        reservationRepository.deleteById(reservation.getId());
        reservationItemService.deleteReservationItem(reservationItem);
    }
}
