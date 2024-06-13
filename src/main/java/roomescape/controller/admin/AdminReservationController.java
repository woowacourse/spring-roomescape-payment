package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.ReservationService;
import roomescape.service.dto.request.ReservationConditionRequest;
import roomescape.service.dto.request.ReservationSaveRequest;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.service.dto.response.ReservationResponses;

import java.net.URI;

@Tag(name = "[ADMIN] 예약 API", description = "어드민 권한으로 예약을 생성/조회/삭제할 수 있습니다.")
@RestController
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "어드민 예약 추가 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "생성된 예약 정보를 반환합니다.")
    })
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @RequestBody @Valid ReservationSaveRequest reservationSaveRequest
    ) {
        ReservationResponse reservationResponse = reservationService.saveAdminReservation(reservationSaveRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "어드민 예약 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "예약 정보를 반환합니다.")
    })
    @GetMapping("/admin/reservations")
    public ResponseEntity<ReservationResponses> getReservations(
            @ModelAttribute @Valid ReservationConditionRequest reservationConditionRequest) {
        ReservationResponses reservationResponses = reservationService.findReservationsByCondition(reservationConditionRequest);

        return ResponseEntity.ok()
                .body(reservationResponses);
    }

    @Operation(summary = "어드민 예약 삭제 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204", description = "예약 삭제에 성공했습니다.")
    })
    @DeleteMapping("/admin/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
