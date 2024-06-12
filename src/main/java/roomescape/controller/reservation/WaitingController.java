package roomescape.controller.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import roomescape.controller.auth.AuthenticationPrincipal;
import roomescape.dto.ErrorResponse;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.service.WaitingService;

@RequestMapping("/waitings")
@RestController
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약 대기 생성")
    @ApiResponse(responseCode = "201", description = "예약 대기 생성 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 대기 생성 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservationWaiting(
            @AuthenticationPrincipal final LoginMember loginMember,
            @RequestBody final ReservationSaveRequest request
    ) {
        final ReservationDto reservationDto = ReservationDto.of(request, loginMember.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(waitingService.createReservationWaiting(reservationDto));
    }
}
