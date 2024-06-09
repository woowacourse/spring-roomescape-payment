package roomescape.service.booking.waiting.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.exception.RoomEscapeException;
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

    public WaitingResponse findWaitingByReservationId(final Long reservationId) {
        Waiting waiting = findByReservationId(reservationId);
        return WaitingResponse.from(waiting);
    }

    private Waiting findByReservationId(final Long reservationId) {
        return waitingRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 예약 대기 정보 입니다.",
                        "reservationId : " + reservationId
                ));
    }
}
