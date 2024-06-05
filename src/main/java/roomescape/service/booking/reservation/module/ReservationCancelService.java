package roomescape.service.booking.reservation.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.waiting.Waiting;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@Service
@Transactional
public class ReservationCancelService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationCancelService(ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        List<Reservation> waitingReservations = findWaitingReservation(reservation);

        reservationRepository.delete(reservation);
        adjustWaitingOrder(waitingReservations);
    }

    private List<Reservation> findWaitingReservation(Reservation reservation) {
        return reservationRepository.findByDateAndTimeIdAndThemeIdAndStatus(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                Status.WAITING
        );
    }

    private void adjustWaitingOrder(List<Reservation> reservationsToAdjust) {
        for (Reservation reservation : reservationsToAdjust) {
            Waiting waiting = waitingRepository.findByReservationIdOrThrow(reservation.getId());
            if (waiting.isFirstOrder()) {
                reservation.changeStatusToReserve();
                waitingRepository.delete(waiting);
            }
            if (!waiting.isFirstOrder()) {
                waiting.decreaseWaitingOrderByOne();
            }
        }
    }
}
