package roomescape.service.booking.waiting;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.waiting.Waiting;
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

    @Transactional
    public ReservationResponse resisterWaiting(ReservationRequest request) {
        Waiting waiting = waitingRegisterService.registerWaiting(request);
        return waitingSearchService.findReservationWaiting(waiting);
    }

    public List<WaitingResponse> findAllWaitingReservations() {
        return waitingSearchService.findAllWaitingReservations()
                .stream()
                .map(WaitingResponse::from)
                .toList();
    }

    @Transactional
    public void cancelWaitingForUser(Long reservationId) {
        waitingCancelService.cancelWaitingForUser(reservationId);
    }

    @Transactional
    public void cancelWaiting(Long waitingId) {
        waitingCancelService.cancelWaiting(waitingId);
    }

    public List<WaitingResponse> findPaymentByReservationIds(final List<Long> waitingReservationIds) {
        return waitingSearchService.findPaymentByReservationIds(waitingReservationIds).stream()
                .map(WaitingResponse::from)
                .toList();
    }
}
