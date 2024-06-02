package roomescape.controller.admin;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationService;
import roomescape.service.dto.ReservationBookedResponse;
import roomescape.service.dto.ReservationConditionRequest;
import roomescape.service.dto.ReservationRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.WaitingResponse;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/booked")
    public ResponseEntity<ReservationResponse> saveReservation(
            @RequestBody @Valid ReservationRequest reservationRequest
    ) {
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationRequest);
        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            return ResponseEntity.created(URI.create("/reservations/booked/" + reservationResponse.id()))
                    .body(reservationResponse);
        }
        return ResponseEntity.created(URI.create("/reservations/waiting/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/booked")
    public ResponseEntity<List<ReservationBookedResponse>> findBookedReservations(
            @ModelAttribute @Valid ReservationConditionRequest reservationConditionRequest) {
        List<ReservationBookedResponse> reservationResponses = reservationService.findBookedReservationsByCondition(reservationConditionRequest);

        return ResponseEntity.ok()
                .body(reservationResponses);
    }

    @DeleteMapping("/booked/{id}")
    public ResponseEntity<Void> deleteBooked(@PathVariable("id") Long id) {
        reservationService.cancelBooked(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<WaitingResponse>> getAllWaiting() {
         List<WaitingResponse> waitingResponses = reservationService.findAllWaiting();

         return ResponseEntity.ok()
                 .body(waitingResponses);
    }
}
