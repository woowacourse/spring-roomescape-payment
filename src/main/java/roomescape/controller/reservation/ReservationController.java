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

import roomescape.controller.auth.AuthenticationPrincipal;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWithPaymentRequest;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;

@RequestMapping("/reservations")
@RestController
public class ReservationController implements ReservationControllerDocs {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(final ReservationService reservationService, final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal final LoginMember loginMember,
            @RequestBody final ReservationWithPaymentRequest request
    ) {
        final PaymentResponse savedPayment = paymentService.createPayment(request);
        final ReservationResponse savedReservation = reservationService.createReservation(request, loginMember.id());
        paymentService.confirm(new PaymentConfirmRequest(savedPayment));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable final Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWithRankResponse>> findMyReservationsAndWaitings(
            @AuthenticationPrincipal final LoginMember loginMember
    ) {
        return ResponseEntity.ok(reservationService.findMyReservationsAndWaitings(loginMember));
    }
}
