package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.ReservationService;
import roomescape.service.WaitingService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public AdminController(ReservationService reservationService, WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createAdminReservation(@RequestBody ReservationRequest request) {
        ReservationResponse reservationResponse = reservationService.createByAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> findAllWaitings() {
        List<WaitingResponse> waitings = waitingService.findEntireReservations();
        return ResponseEntity.ok(waitings);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> cancelWaiting(@PathVariable Long id) {
        waitingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
