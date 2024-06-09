package roomescape.web.controller.reservation;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.login.LoginMember;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationRequest;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.dto.waiting.UserReservationWaitingRequest;
import roomescape.service.reservation.ReservationCancelService;
import roomescape.service.reservation.ReservationRegisterService;
import roomescape.service.reservation.ReservationSearchService;

@RestController
@RequestMapping("/reservations")
class ReservationController {

    private final ReservationRegisterService registerService;
    private final ReservationSearchService searchService;
    private final ReservationCancelService cancelService;

    public ReservationController(ReservationRegisterService registerService,
                                 ReservationSearchService searchService,
                                 ReservationCancelService cancelService
    ) {
        this.registerService = registerService;
        this.searchService = searchService;
        this.cancelService = cancelService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody UserReservationRequest userRequest,
                                                                 LoginMember loginMember
    ) {
        ReservationRequest reservationRequest = userRequest.toReservationRequest(loginMember.id());
        PaymentRequest paymentRequest = userRequest.toPaymentRequest();
        ReservationResponse response = registerService.registerReservation(reservationRequest, paymentRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createWaitingReservation(@RequestBody UserReservationWaitingRequest userRequest,
                                                                        LoginMember loginMember
    ) {
        ReservationRequest reservationRequest = userRequest.toReservationRequest(loginMember.id());
        ReservationResponse response = registerService.registerWaitingReservation(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<ReservationResponse> requestReservationPayment(@RequestBody PaymentRequest paymentRequest,
                                                                         @PathVariable Long id) {
        ReservationResponse response = registerService.requestPaymentByPaymentPending(id, paymentRequest);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        ReservationResponse response = searchService.findReservation(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> responses = searchService.findAllReservedReservations();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<UserReservationResponse>> getReservationsByUser(LoginMember loginMember) {
        List<UserReservationResponse> responses = searchService.findReservationByMemberId(loginMember.id());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        cancelService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
