package roomescape.web.controller.api;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationWaitingService;
import roomescape.service.response.ReservationWaitingDto;
import roomescape.web.controller.response.ReservationWaitingResponse;

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
