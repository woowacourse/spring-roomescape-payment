package roomescape.presentation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.Authenticated;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.MyReservationsResponse;
import roomescape.dto.response.ReservationWithPaymentResponse;
import roomescape.service.ReservationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationWithPaymentResponse> createNewReservation(
            @Authenticated Long memberId,
            @Valid @RequestBody ReservationCreateRequest request) {
        ReservationWithPaymentResponse response = reservationService.createReservationForMember(
                memberId, request);

        return ResponseEntity
                .created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public List<MyReservationsResponse> getMyBookingHistory(@Authenticated Long id) {
        return reservationService.findBookingHistory(id);
    }
}
