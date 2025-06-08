package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.dto.ErrorResponse;
import roomescape.reservation.controller.dto.AdminCreateReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;

@Tag(name = "어드민 예약 API")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자 예약 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "관리자가 예약을 성공적으로 생성한다."),
            @ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않아 예약 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "비즈니스 조건 충돌로 인해 예약 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류로 인해 예약 생성에 실패한다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid final AdminCreateReservationRequest request
    ) {
        ReservationResponse response = reservationService.createReservationByAdmin(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
