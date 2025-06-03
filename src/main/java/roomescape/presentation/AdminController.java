package roomescape.presentation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.request.AdminReservationCreateRequest;
import roomescape.dto.request.ReservationCondition;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.ReservationService;
import roomescape.service.WaitingService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public AdminController(final ReservationService reservationService, final WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody AdminReservationCreateRequest request) {
        ReservationResponse reservationResponse = reservationService.createReservation(
                request.memberId(), request.timeId(), request.themeId(), request.date()
        );
        return ResponseEntity
                .created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> getReservations(@ModelAttribute ReservationCondition reservationCondition) {
        return reservationService.findReservations(reservationCondition);
    }

    @GetMapping("/waitings")
    public List<WaitingResponse> getWaitings() {
        return waitingService.findAll();
    }
}
