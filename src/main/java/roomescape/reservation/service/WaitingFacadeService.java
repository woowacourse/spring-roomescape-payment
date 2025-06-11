package roomescape.reservation.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;

@Service
public class WaitingFacadeService {
    private final WaitingService waitingService;

    public WaitingFacadeService(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    public ReservationResponse createWaiting(final ReservationRequest request, final Long memberId) {
        Waiting waiting = waitingService.createWaiting(request, memberId);
        return ReservationResponse.of(waiting);
    }

    public List<ReservationResponse> findWaitings() {
        List<Waiting> waitings = waitingService.findWaitings();
        return waitings.stream()
                .map(ReservationResponse::of)
                .toList();
    }

    public void deleteWaiting(final Long id) {
        waitingService.delete(id);
    }
}
