package roomescape.controller.reservation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;

@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(final ReservationService reservationService, final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @Operation(summary = "예약 생성")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @ApiResponse(
            responseCode = "400",
            description = "예약 생성 실패 - 결제 승인 요청 시간 만료",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "예약 생성 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal final LoginMember loginMember,
            @RequestBody final ReservationSaveRequest request
    ) {
        final ReservationDto reservationDto = ReservationDto.of(request, loginMember.id());
        final ReservationResponse reservationResponse = reservationService.createReservation(reservationDto);
        final PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(request,
                reservationResponse.id());
        paymentService.confirm(paymentConfirmRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponse);
    }

    @Operation(summary = "예약 전체 조회")
    @ApiResponse(responseCode = "200", description = "예약 조회 성공")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @Operation(summary = "예약 삭제")
    @ApiResponse(responseCode = "204", description = "예약 삭제 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 삭제 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable final Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 예약 조회")
    @ApiResponse(responseCode = "200", description = "예약 조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "예약 조회 실패 - 존재하지 않는 id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWithRankResponse>> findMyReservationsAndWaitings(
            @AuthenticationPrincipal final LoginMember loginMember
    ) {
        return ResponseEntity.ok(reservationService.findMyReservationsAndWaitings(loginMember));
    }
}
