package roomescape.service.booking.reservation.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.waiting.Waiting;
import roomescape.exception.custom.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@Service
public class ReservationCancelService {

    private final ReservationSearchService reservationSearchService;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationCancelService(final ReservationSearchService reservationSearchService,
                                    ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.reservationSearchService = reservationSearchService;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    @Transactional
    public void deleteReservation(Reservation reservation) {
        List<Reservation> waitingReservations = findWaitingReservation(reservation);
        reservation.delete();
        adjustWaitingOrder(waitingReservations);
    }

    @Transactional
    public void deleteReservation(final Long id) {
        Reservation reservation = reservationSearchService.findReservationById(id);
        deleteReservation(reservation);
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
            Waiting waiting = findWaitingByReservationId(reservation.getId());
            if (waiting.isFirstOrder()) {
                reservation.changeToPending();
                waitingRepository.delete(waiting);
            }
            if (!waiting.isFirstOrder()) {
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
}
