package roomescape.waiting.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.waiting.dto.AdminReservationWaitingResponse;
import roomescape.waiting.dto.ReservationWaitingRequest;
import roomescape.waiting.dto.ReservationWaitingResponse;
import roomescape.waiting.service.ReservationWaitingService;

@RestController
@RequiredArgsConstructor
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    @PostMapping("/reservations-waiting")
    public ResponseEntity<ReservationWaitingResponse> addReservationWaiting(
            @RequestBody @Valid final ReservationWaitingRequest request, final Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationWaitingService.addReservationWaiting(request, memberId));
    }

    @DeleteMapping("/reservations-waiting/{id}")
    public ResponseEntity<Void> removeReservationWaiting(@PathVariable(name = "id") final long id) {
        reservationWaitingService.removeReservationWaiting(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/admin/reservations-waiting")
    public ResponseEntity<List<AdminReservationWaitingResponse>> getReservationsWaitingForAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationWaitingService.getAllReservationWaiting());
    }

    @DeleteMapping("/admin/reservations-waiting/{id}")
    public ResponseEntity<Void> removeReservationWaitingForAdmin(@PathVariable final long id) {
        reservationWaitingService.removeReservationWaiting(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
