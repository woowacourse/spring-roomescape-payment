package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ReservationService;
import roomescape.service.dto.request.ReservationConditionRequest;
import roomescape.service.dto.request.ReservationSaveRequest;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.service.dto.response.ReservationResponses;

import java.net.URI;

@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "어드민 예약 추가 API", description = "어드민이 예약을 추가한다.")
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @RequestBody @Valid ReservationSaveRequest reservationSaveRequest
    ) {
        ReservationResponse reservationResponse = reservationService.saveAdminReservation(reservationSaveRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "어드민 예약 조회 API", description = "어드민이 예약을 조회한다.")
    @GetMapping("/admin/reservations")
    public ResponseEntity<ReservationResponses> getReservations(
            @ModelAttribute @Valid ReservationConditionRequest reservationConditionRequest) {
        ReservationResponses reservationResponses = reservationService.findReservationsByCondition(reservationConditionRequest);

        return ResponseEntity.ok()
                .body(reservationResponses);
    }

    @Operation(summary = "어드민 예약 삭제 API", description = "어드민이 예약을 삭제한다.")
    @DeleteMapping("/admin/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
