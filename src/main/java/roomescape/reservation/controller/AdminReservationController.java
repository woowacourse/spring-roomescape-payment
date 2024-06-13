package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.controller.dto.request.ReservationSaveRequest;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.service.component.ReservationComponentService;

@RestController
public class AdminReservationController {

    private final ReservationComponentService reservationComponentService;

    public AdminReservationController(ReservationComponentService reservationComponentService) {
        this.reservationComponentService = reservationComponentService;
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> save(@Valid @RequestBody ReservationSaveRequest saveRequest) {
        ReservationResponse response = reservationComponentService.saveWithoutPayment(saveRequest);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }
}
