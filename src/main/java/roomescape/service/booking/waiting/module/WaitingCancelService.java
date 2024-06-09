package roomescape.service.booking.waiting.module;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@Service
public class WaitingCancelService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;

    public WaitingCancelService(WaitingRepository waitingRepository, ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
    }

    public void cancelWaitingForUser(Long reservationId) {
        Waiting waiting = findWaitingByReservationId(reservationId);
        cancelWaiting(waiting.getId());
    }

    public void cancelWaiting(Long waitingId) {
        Waiting waiting = findWaitingById(waitingId);
        Reservation reservation = waiting.getReservation();

        List<Reservation> waitings = findWaitingReservationBySameConditions(reservation);
        adjustWaitingOrder(waitings, waiting.getWaitingOrderValue());

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

    private void adjustWaitingOrder(List<Reservation> waitingsToAdjust, int waitingOrderToDelete) {
        for (Reservation waitingReservation : waitingsToAdjust) {
            Waiting waiting = findWaitingByReservationId(waitingReservation.getId());
            if (waiting.isWaitingOrderGreaterThan(waitingOrderToDelete)) {
                waiting.decreaseWaitingOrderByOne();
            }
        }
    }

    private Waiting findWaitingByReservationId(Long reservationId) {
        return waitingRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약 정보와 일치하는 대기 정보가 존재하지 않습니다.",
                        "reservation_id : " + reservationId
                ));
    }

    private Waiting findWaitingById(Long waitingId) {
        return waitingRepository.findById(waitingId)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약 대기 정보가 존재하지 않습니다.",
                        "waiting_id : " + waitingId
                ));
    }
}
