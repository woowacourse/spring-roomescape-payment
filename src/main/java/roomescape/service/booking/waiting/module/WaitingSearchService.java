package roomescape.service.booking.waiting.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.exception.custom.RoomEscapeException;
import roomescape.repository.WaitingRepository;

@Service
@Transactional(readOnly = true)
public class WaitingSearchService {

    private final WaitingRepository waitingRepository;

    public WaitingSearchService(WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public List<Waiting> findAllWaitingReservations() {
        return waitingRepository.findAll();
    }

    public ReservationResponse findReservationWaiting(final Waiting waiting) {
        return ReservationResponse.from(waiting.getReservation());
    }

    public List<Waiting> findWaitingByReservationIds(final List<Long> reservationIds) {
        return waitingRepository.findByReservationIdIn(reservationIds);
    }

    public Waiting findWaitingById(Long waitingId) {
        return waitingRepository.findById(waitingId)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약 대기 정보가 존재하지 않습니다.",
                        "waiting_id : " + waitingId
                ));
    }

    public Waiting findWaitingByReservationId(Long reservationId) {
        return waitingRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약 정보와 일치하는 대기 정보가 존재하지 않습니다.",
                        "reservation_id : " + reservationId
                ));
    }
}
