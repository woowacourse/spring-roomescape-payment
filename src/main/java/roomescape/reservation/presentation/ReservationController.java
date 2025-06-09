package roomescape.reservation.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationAndWaitingResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

import java.net.URI;
import java.util.List;

import static roomescape.reservation.presentation.ReservationController.RESERVATION_BASE_URL;

@RestController
@RequestMapping(RESERVATION_BASE_URL)
public class ReservationController {

    public static final String RESERVATION_BASE_URL = "/reservations";
    private static final String SLASH = "/";

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(final ReservationService reservationService, final PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute final ReservationConditionRequest request) {
        List<ReservationResponse> response = reservationService.getReservations(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody final ReservationRequest request,
            @Login final LoginMember loginMember
    ) {
        ReservationResponse response = reservationService.createPendingReservation(request, loginMember.id());
        TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(request.orderId(), request.amount(), request.paymentKey());
        paymentService.confirmAndSavePayment(tossPaymentConfirmRequest, response.id());

        URI locationUri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable("id") final Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationAndWaitingResponse>> getMyReservations(@Login LoginMember loginMember) {
        List<MyReservationAndWaitingResponse> myReservationResponses = reservationService.getMyReservations(loginMember.id());
        return ResponseEntity.ok().body(myReservationResponses);
    }
}
