package roomescape.controller.admin;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ReservationService;
import roomescape.service.dto.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> saveReservation(
            @RequestBody @Valid ReservationRequest reservationRequest
    ) {
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationRequest);
        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                    .body(reservationResponse);
        }
        return ResponseEntity.created(URI.create("/reservations/waiting/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping
    public ResponseEntity<List<ReservationBookedResponse>> getReservations(
            @ModelAttribute @Valid ReservationConditionRequest reservationConditionRequest) {
        List<ReservationBookedResponse> reservationResponses = reservationService.findReservationsByCondition(reservationConditionRequest);

        return ResponseEntity.ok()
                .body(reservationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<WaitingResponse>> getAllWaiting() {
         List<WaitingResponse> waitingResponses = reservationService.findAllWaiting();

         return ResponseEntity.ok()
                 .body(waitingResponses);
    }
}
