package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.WaitingReservationPaymentRequest;
import roomescape.reservation.service.WaitingReservationService;

import java.net.URI;

@RestController
@RequestMapping("/reservations/waiting")
@Tag(name = "Waiting Reservation API", description = "예약 대기 관련 API")
public class WaitingReservationController {

    private final WaitingReservationService waitingReservationService;

    public WaitingReservationController(WaitingReservationService waitingReservationService) {
        this.waitingReservationService = waitingReservationService;
    }

    @PostMapping
    @Operation(summary = "예약 대기를 추가한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "예약 정보가 잘못된 경우 발생")
    })
    @Parameters({
            @Parameter(name = "authInfo", description = "로그인한 유저의 정보", required = true),
            @Parameter(name = "reservationRequest", description = "예약 정보 DTO", required = true)
    })
    public ResponseEntity<ReservationResponse> reserveWaiting(
            @LoginUser AuthInfo authInfo,
            @RequestBody @Valid ReservationRequest reservationRequest) {
        ReservationResponse response = waitingReservationService.reserveWaiting(reservationRequest, authInfo.getId());
        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId())).body(response);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "예약 대기에서 예약 상태로 변경한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "400", description = "예약 정보가 잘못된 경우 발생")
    })
    @Parameters({
            @Parameter(name = "authInfo", description = "로그인한 유저의 정보", required = true),
            @Parameter(name = "waitingReservationPaymentRequest", description = "예약 및 결제 정보 DTO, 예약 정보를 문자열로 받음", required = true)
    })
    public ReservationResponse confirmReservation(@LoginUser AuthInfo authInfo,
                                                     @RequestBody @Valid WaitingReservationPaymentRequest waitingReservationPaymentRequest) {
        return waitingReservationService.confirmReservation(waitingReservationPaymentRequest, authInfo.getId());
    }
}
