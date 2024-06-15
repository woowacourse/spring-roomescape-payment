package roomescape.controller.admin;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationService;
import roomescape.dto.request.reservation.AdminReservationRequest;
import roomescape.dto.request.reservation.ReservationCriteriaRequest;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.service.ReservationWaitingService;

@RestController
@RequestMapping("/admin")
public class AdminReservationController {
    private final ReservationService reservationService;
    private final ReservationWaitingService reservationWaitingService;

    public AdminReservationController(ReservationService reservationService,
                                      ReservationWaitingService reservationWaitingService) {
        this.reservationService = reservationService;
        this.reservationWaitingService = reservationWaitingService;
    }

    @GetMapping("/waitings")
    public ResponseEntity<List<ReservationResponse>> findAllWaitings() {
        List<ReservationResponse> responses = reservationWaitingService.findAll();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(@RequestBody @Valid AdminReservationRequest adminReservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveReservationByAdmin(adminReservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations/search")
    public ResponseEntity<List<ReservationResponse>> searchAdmin(ReservationCriteriaRequest reservationCriteriaRequest) {
        List<ReservationResponse> responses = reservationService.findByCriteria(reservationCriteriaRequest);
        return ResponseEntity.ok(responses);
    }
}
