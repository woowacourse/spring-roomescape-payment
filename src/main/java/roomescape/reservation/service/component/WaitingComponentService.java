package roomescape.reservation.service.component;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.controller.dto.response.WaitingResponse;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.service.dto.request.WaitingReservationRequest;
import roomescape.reservation.service.module.ReservationCreateService;
import roomescape.reservation.service.module.WaitingPlanService;
import roomescape.reservation.service.module.WaitingQueryService;

@Service
public class WaitingComponentService {

    private final WaitingQueryService waitingQueryService;
    private final ReservationCreateService reservationCreateService;
    private final WaitingPlanService waitingPlanService;

    public WaitingComponentService(
            WaitingQueryService waitingQueryService,
            ReservationCreateService reservationCreateService,
            WaitingPlanService waitingPlanService
    ) {
        this.waitingQueryService = waitingQueryService;
        this.reservationCreateService = reservationCreateService;
        this.waitingPlanService = waitingPlanService;
    }

    @Transactional
    public ReservationResponse save(WaitingReservationRequest request) {
        Waiting waiting = reservationCreateService.createWaiting(request);
        waitingPlanService.validateSaveWaiting(waiting);
        Waiting savedWaiting = waitingQueryService.save(waiting);

        return ReservationResponse.toResponse(savedWaiting);
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findWaitings() {
        return waitingQueryService.findWaitings()
                .stream()
                .map(WaitingResponse::toResponse)
                .toList();
    }

    @Transactional
    public void approveReservation(Long id) {
        Waiting waiting = waitingQueryService.findById(id);
        waitingPlanService.validateApproveReservation(waiting);
        waiting.updatePaymentPending();
    }

    public void delete(Long id) {
        waitingQueryService.delete(id);
    }
}
