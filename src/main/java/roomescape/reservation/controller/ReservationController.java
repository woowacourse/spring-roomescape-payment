package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationPaymentService;
import roomescape.reservation.service.ReservationService;

@Tag(name = "예약 API", description = "회원 예약 API 입니다.")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationPaymentService reservationPaymentService;

    public ReservationController(ReservationService reservationService,
                                 ReservationPaymentService reservationPaymentService) {
        this.reservationService = reservationService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReservationResponse.class)))})
    @Operation(summary = "예약 탐색", description = "모든 예약들을 탐색합니다.")
    @GetMapping
    public List<ReservationResponse> findReservations() {
        return reservationService.findReservations();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "500", description = "예약 생성에 실패했습니다.", content = @Content(schema = @Schema(implementation = IllegalArgumentException.class)))})
    @Operation(summary = "예약 생성", description = "정보를 사용하여 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationCreateRequest request,
            LoggedInMember member) {
        ReservationResponse response = reservationPaymentService.saveReservationWithPayment(request, member);

        URI location = URI.create("/reservations/" + response.id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "500", description = "해당 예약은 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ReservationResponse.class)))})
    @Operation(summary = "예약 삭제", description = "단일 예약을 삭제 합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteWaiting(id);
    }
}
