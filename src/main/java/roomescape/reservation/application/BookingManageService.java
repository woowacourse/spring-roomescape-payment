package roomescape.reservation.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ViolationException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.event.ReservationFailedEvent;
import roomescape.reservation.event.ReservationSavedEvent;

import java.util.Optional;

@Service
public class BookingManageService extends ReservationManageService {
    private final ApplicationEventPublisher eventPublisher;

    public BookingManageService(ReservationRepository reservationRepository, ApplicationEventPublisher eventPublisher) {
        super(reservationRepository);
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Reservation createWithPayment(Reservation reservation, ConfirmedPayment confirmedPayment) {
        eventPublisher.publishEvent(new ReservationFailedEvent(confirmedPayment));

        Reservation savedReservation = create(reservation);
        eventPublisher.publishEvent(new ReservationSavedEvent(savedReservation, confirmedPayment));
        return savedReservation;
    }

    @Override
    protected void correctReservationStatus(int bookingCount, Reservation reservation) {
        if (bookingCount > MAX_RESERVATION_NUMBER_IN_TIME_SLOT) {
            reservation.changeToWaiting();
            reservationRepository.updateStatusById(ReservationStatus.WAITING, reservation.getId());
        }
    }

    @Override
    protected void scheduleAfterDeleting(Reservation deletedReservation) {
        Optional<Reservation> firstWaitingReservation = reservationRepository.findFirstByDateAndTimeAndThemeAndStatusOrderById(
                deletedReservation.getDate(), deletedReservation.getTime(),
                deletedReservation.getTheme(), ReservationStatus.WAITING);
        firstWaitingReservation.ifPresent(Reservation::changeToBooking);
    }

    @Override
    protected void validateReservationStatus(Reservation reservation) {
        if (!reservation.isBooking()) {
            throw new ViolationException("예약 상태가 예약 중이 아닙니다.");
        }
    }

    @Override
    protected void validatePermissionForDeleting(Reservation reservation, Member agent) {
        if (!agent.isAdmin()) {
            throw new ViolationException("예약을 삭제할 권한이 없습니다. 관리자만 삭제할 수 있습니다.");
        }
    }
}