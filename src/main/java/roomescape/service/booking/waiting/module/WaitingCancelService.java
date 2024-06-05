package roomescape.service.booking.waiting.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@Service
@Transactional
public class WaitingCancelService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;

    public WaitingCancelService(WaitingRepository waitingRepository, ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
    }

    public void cancelWaitingForUser(Long reservationId) {
        Waiting waiting = waitingRepository.findByReservationIdOrThrow(reservationId);
        cancelWaiting(waiting.getId());
    }

    public void cancelWaiting(Long waitingId) {
        Waiting waiting = waitingRepository.findByIdOrThrow(waitingId);
        Reservation reservation = waiting.getReservation();

        List<Reservation> waitingReservations = findWaitingReservationBySameConditions(reservation);
        adjustWaitingOrder(waitingReservations, waiting.getWaitingOrderValue());
        waitingRepository.delete(waiting);
        reservationRepository.delete(reservation);
    }

    private List<Reservation> findWaitingReservationBySameConditions(Reservation reservation) {
        return reservationRepository.findByDateAndTimeIdAndThemeIdAndStatus(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                reservation.getStatus()
        );
    }

    private void adjustWaitingOrder(List<Reservation> reservationsToAdjust, int waitingOrderToDelete) {
        for (Reservation reservation : reservationsToAdjust) {
            Waiting waiting = waitingRepository.findByReservationIdOrThrow(reservation.getId());
            if (waiting.isWaitingOrderGreaterThan(waitingOrderToDelete)) {
                waiting.decreaseWaitingOrderByOne();
            }
        }
    }
}
