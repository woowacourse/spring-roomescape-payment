package roomescape.reservation.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.auth.principal.AuthenticatedMember;
import roomescape.reservation.dto.*;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.Waiting;
import roomescape.reservation.service.PaymentService;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.WaitingService;
import roomescape.resolver.Authenticated;

@RestController
@Tag(name = "회원 예약", description = "회원 예약 관련 API")
public class ReservationController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;
    private final PaymentService paymentService;

    public ReservationController(final ReservationService reservationService, final WaitingService waitingService, final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
        this.paymentService = paymentService;
    }

    @GetMapping("/reservations")
    @Operation(summary = "전체 예약 조회")
    public List<ReservationResponse> getReservations() {
        return reservationService.getReservations()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/reservations")
    @Operation(summary = "예약 추가")
    @ApiResponse(responseCode = "201", description = "예약 추가 성공")
    @ApiResponse(responseCode = "400", description = "결제 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "결제 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ReservationResponse> saveReservation(
            @Valid @RequestBody final SaveReservationRequest request,
            @Authenticated final AuthenticatedMember authenticatedMember
    ) {
        final Reservation savedReservation = reservationService.saveReservation(
                request, authenticatedMember.id());

        return ResponseEntity.created(URI.create("/reservations/" + savedReservation.getId()))
                .body(ReservationResponse.from(savedReservation));
    }

    @GetMapping("/reservations-mine")
    @Operation(summary = "내 예약 내역 조회", description = "내 예약 확정, 대기 내역 조회")
    public List<MyReservationResponse> getMyReservations(@Authenticated final AuthenticatedMember authenticatedMember) {
        return reservationService.getMyReservations(authenticatedMember.id());
    }

    @PostMapping("/reservations-waiting")
    @Operation(summary = "예약 대기 추가")
    @ApiResponse(responseCode = "201", description = "예약 대기 추가 성공")
    public ResponseEntity<SaveWaitingResponse> saveWaiting(
            @RequestBody final SaveWaitingRequest request,
            @Authenticated final AuthenticatedMember authenticatedMember
    ) {
        Waiting savedWaiting = waitingService.saveWaiting(request, authenticatedMember.id());

        return ResponseEntity.created(URI.create("/reservations-waiting/" + savedWaiting.getId()))
                .body(SaveWaitingResponse.from(savedWaiting));
    }

    @DeleteMapping("/reservations-mine/{waiting-id}")
    @Operation(summary = "예약 대기 삭제")
    @ApiResponse(responseCode = "204", description = "예약 대기 삭제 성공")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("waiting-id") final Long waitingId) {
        waitingService.deleteWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
