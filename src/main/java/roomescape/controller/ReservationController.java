package roomescape.controller;

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

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<Reservation> allReservations = reservationReadService.findAllReservations();
        List<ReservationResponse> responses = allReservations.stream()
                .map(ReservationResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MemberReservationResponse>> getMemberReservations(HttpServletRequest request) {
        Long memberId = authService.getMemberIdByCookie(request.getCookies());
        List<MemberReservationResponse> responses = reservationWaitingReadService.getAllMemberReservationsAndWaiting(memberId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request,
                                                                 @AuthenticationPrincipal Member member) {
        Reservation reservation = reservationPaymentService.payReservation(request, member);
        ReservationResponse reservationResponse = new ReservationResponse(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservationResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") long id) {
        paymentService.cancelPayment(id);
        reservationWaitingWriteService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reservations-payment")
    public ResponseEntity<ReservationResponse> createReservationWithPayment(@RequestBody ReservationWithPaymentRequest request,
                                                                            @AuthenticationPrincipal Member member) {
        Reservation reservation = reservationPaymentService.payReservationWithoutPayment(request, member);
        ReservationResponse reservationResponse = new ReservationResponse(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId())).body(reservationResponse);
    }
}
