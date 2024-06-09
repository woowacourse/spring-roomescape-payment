package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
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
}
