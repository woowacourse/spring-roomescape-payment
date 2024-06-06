package roomescape.web.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ReservationWaitingService;
import roomescape.service.response.ReservationWaitingDto;
import roomescape.web.controller.response.ReservationWaitingResponse;

import java.util.List;

@RestController
@RequestMapping("/admin/reservation-waitings")
public class AdminReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public AdminReservationWaitingController(final ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationWaitingResponse>> getAvailableWaitings() {
        List<ReservationWaitingResponse> waitingWebResponses = reservationWaitingService.findAllAllowed()
                .stream()
                .map(ReservationWaitingResponse::new)
                .toList();

        return ResponseEntity.ok().body(waitingWebResponses);
    }

    @PatchMapping("/{id}/deny")
    public ResponseEntity<ReservationWaitingResponse> updateWaitingStatus(@PathVariable Long id) {
        ReservationWaitingDto waitingAppResponse = reservationWaitingService.denyWaiting(id);
        ReservationWaitingResponse waitingWebResponse = new ReservationWaitingResponse(waitingAppResponse);

        return ResponseEntity.ok(waitingWebResponse);
    }
}