package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.annotation.AuthenticationPrincipal;
import roomescape.controller.request.ReservationRequest;
import roomescape.controller.request.ReservationWithPaymentRequest;
import roomescape.controller.response.MemberReservationResponse;
import roomescape.controller.response.ReservationResponse;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.service.*;

import java.net.URI;
import java.util.List;

@Tag(name = "reservation", description = "예약 및 결제 API")
@RestController
public class ReservationController {

    private final ReservationReadService reservationReadService;
    private final AuthService authService;
    private final ReservationWaitingReadService reservationWaitingReadService;
    private final ReservationWaitingWriteService reservationWaitingWriteService;
    private final PaymentService paymentService;
    private final ReservationPaymentService reservationPaymentService;

    public ReservationController(ReservationReadService reservationReadService,
                                 AuthService authService,
                                 ReservationWaitingReadService reservationWaitingReadService,
                                 ReservationWaitingWriteService reservationWaitingWriteService,
                                 PaymentService paymentService,
                                 ReservationPaymentService reservationPaymentService) {
        this.reservationReadService = reservationReadService;
        this.authService = authService;
        this.reservationWaitingReadService = reservationWaitingReadService;
        this.reservationWaitingWriteService = reservationWaitingWriteService;
        this.paymentService = paymentService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @Operation(summary = "예약 조회", description = "모든 예약을 조회합니다.")
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<Reservation> allReservations = reservationReadService.findAllReservations();
        List<ReservationResponse> responses = allReservations.stream()
                .map(ReservationResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "내 예약 및 예약 대기 조회", description = "로그인 정보에 따라 내 예약과 예약 대기를 조회합니다.")
    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MemberReservationResponse>> getMemberReservations(HttpServletRequest request) {
        Long memberId = authService.getMemberIdByCookie(request.getCookies());
        List<MemberReservationResponse> responses = reservationWaitingReadService.getAllMemberReservationsAndWaiting(memberId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 등록", description = "선택한 방탈출로 예약을 등록합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request,
                                                                 @AuthenticationPrincipal Member member) {
        Reservation reservation = reservationPaymentService.payReservation(request, member);
        ReservationResponse reservationResponse = new ReservationResponse(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservationResponse);
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        paymentService.cancelPayment(id);
        reservationWaitingWriteService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 결제", description = "결제 대기 상태의 예약을 결제합니다.")
    @PostMapping("/reservations-payment")
    public ResponseEntity<ReservationResponse> createReservationWithPayment(@RequestBody ReservationWithPaymentRequest request,
                                                                            @AuthenticationPrincipal Member member) {
        Reservation reservation = reservationPaymentService.payReservationWithoutPayment(request, member);
        ReservationResponse reservationResponse = new ReservationResponse(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservationResponse);
    }
}
