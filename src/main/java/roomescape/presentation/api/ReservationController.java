package roomescape.presentation.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationService;
import roomescape.application.WaitingService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.PaymentProcessRequest;
import roomescape.presentation.dto.request.ReservationCreateRequest;
import roomescape.presentation.dto.request.ReservationWithPaymentRequest;
import roomescape.presentation.dto.response.ErrorResponse;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.WaitingResponse;

@Tag(name = "예약 관련 기능 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public ReservationController(ReservationService reservationService,
                                 WaitingService waitingService
    ) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @PostMapping
    @Operation(summary = "예약 및 결제 승인")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 및 결제 성공"),
            @ApiResponse(responseCode = "400", description = "결제 승인 중 문제가 발생한 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationWithPaymentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        PaymentProcessRequest paymentRequest = request.toPaymentProcessRequest();
        ReservationResponse response = reservationService.createMemberReservation(request, paymentRequest, loginMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.cancelReservationById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/waitings")
    @Operation(summary = "예약 대기")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 대기 성공"),
            @ApiResponse(responseCode = "409", description = "현재 시간 이전이거나, 이미 예약 또는 대기가 존재하는 경우",  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody @Valid ReservationCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        WaitingResponse response = waitingService.createWaiting(request, loginMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/waitings/{id}")
    @Operation(summary = "예약 대기 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 대기 취소 성공"),
            @ApiResponse(responseCode = "404", description = "예약 대기 건이 존재하지 않는 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "내 예약 건이 아닌 경우", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteWaiting(
            @Parameter(example = "1", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal LoginMember loginMember
    ) {
        waitingService.deleteWaitingByIdAndMember(id, loginMember);

        return ResponseEntity.noContent().build();
    }
}
