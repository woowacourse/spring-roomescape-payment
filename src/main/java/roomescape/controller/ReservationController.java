package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.LoginMemberConverter;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.client.PaymentClient;
import roomescape.service.ReservationService;

@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final PaymentClient tossPaymentClient;

    public ReservationController(ReservationService reservationService, PaymentClient tossPaymentClient) {
        this.reservationService = reservationService;
        this.tossPaymentClient = tossPaymentClient;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllByReservation() {
        List<ReservationResponse> responses = reservationService.findAllByStatus(Status.RESERVATION);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid ReservationRequest reservationRequest) {
        PaymentRequest paymentRequest = new PaymentRequest(reservationRequest.orderId(), reservationRequest.amount(),
                reservationRequest.paymentKey());
        tossPaymentClient.requestPayment(paymentRequest);
        ReservationResponse response = reservationService.saveReservationByClient(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @PostMapping("/waitings")
    public ResponseEntity<ReservationResponse> saveWaitingByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody @Valid WaitingRequest waitingRequest
    ) {
        ReservationResponse response = reservationService.saveWaitingByClient(loginMember, waitingRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @DeleteMapping(value = {"/reservations/{id}", "/waitings/{id}"})
    public ResponseEntity<Void> deleteByReservation(@PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(
            @LoginMemberConverter LoginMember loginMember) {
        List<MyReservationResponse> responses = reservationService.findMyReservations(loginMember.id());
        return ResponseEntity.ok(responses);
    }
}
