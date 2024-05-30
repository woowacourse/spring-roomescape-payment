package roomescape.service.booking.waiting.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.repository.WaitingRepository;

@Service
@Transactional(readOnly = true)
public class WaitingSearchService {

    private final WaitingRepository waitingRepository;

    public WaitingSearchService(WaitingRepository waitingRepository) {
        this.waitingRepository = waitingRepository;
    }

    public List<WaitingResponse> findAllWaitingReservations() {
        return waitingRepository.findAll()
                .stream()
                .map(WaitingResponse::from)
                .toList();
    }

    public ReservationResponse findReservationWaiting(final Long waitingId) {
        Waiting waiting = findWaitingById(waitingId);
        return ReservationResponse.from(waiting.getReservation());
    }

    private Waiting findWaitingById(final Long waitingId) {
        return waitingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "[ERROR] 잘못된 예약 대기 정보 입니다.",
                        new Throwable("waiting_id : " + waitingId)
                ));
    }
}
