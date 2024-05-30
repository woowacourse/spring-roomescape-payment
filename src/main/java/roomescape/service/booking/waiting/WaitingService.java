package roomescape.service.booking.waiting;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.service.booking.waiting.module.WaitingCancelService;
import roomescape.service.booking.waiting.module.WaitingRegisterService;
import roomescape.service.booking.waiting.module.WaitingSearchService;

@Service
public class WaitingService {

    private final WaitingRegisterService waitingRegisterService;
    private final WaitingSearchService waitingSearchService;
    private final WaitingCancelService waitingCancelService;

    public WaitingService(WaitingRegisterService waitingRegisterService,
                          WaitingSearchService waitingSearchService,
                          WaitingCancelService waitingCancelService
    ) {
        this.waitingRegisterService = waitingRegisterService;
        this.waitingSearchService = waitingSearchService;
        this.waitingCancelService = waitingCancelService;
    }

    public ReservationResponse resisterWaiting(ReservationRequest request) {
        Long id = waitingRegisterService.registerWaiting(request);
        return waitingSearchService.findReservationWaiting(id);
    }

    public List<WaitingResponse> findAllWaitingReservations() {
        return waitingSearchService.findAllWaitingReservations();
    }
    
    public void cancelWaitingForUser(Long reservationId) {
        waitingCancelService.cancelWaitingForUser(reservationId);
    }

    public void cancelWaiting(Long waitingId) {
        waitingCancelService.cancelWaiting(waitingId);
    }
}
