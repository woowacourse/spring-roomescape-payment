package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Login;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.reservation.client.PaymentClient;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationSearchRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.PaymentResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationApiController {

    private final ReservationService reservationService;
    private final PaymentClient paymentClient;

    public ReservationApiController(ReservationService reservationService, PaymentClient paymentClient) {
        this.reservationService = reservationService;
        this.paymentClient = paymentClient;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> reservationResponses = reservationService.findAll();

        return ResponseEntity.ok(reservationResponses);
    }

    @GetMapping("/reservations/search")
    public ResponseEntity<List<ReservationResponse>> findAllBySearchCond(
            @Valid @ModelAttribute ReservationSearchRequest reservationSearchRequest
    ) {
        List<ReservationResponse> reservationResponses = reservationService.findAllBySearch(reservationSearchRequest);

        return ResponseEntity.ok(reservationResponses);
    }

    @PostMapping(path = {"/reservations", "/admin/reservations"})
    public ResponseEntity<ReservationResponse> createMemberReservation(
            @Valid @RequestBody ReservationCreateRequest reservationCreateRequest,
            @Login LoginMemberInToken loginMemberInToken
    ) {

        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        PaymentResponse paymentResponse = paymentClient.paymentReservation(authorizations, PaymentRequest.toRequest(reservationCreateRequest)).getBody();

        Long id = reservationService.save(reservationCreateRequest, loginMemberInToken);
        ReservationResponse reservationResponse = reservationService.findById(id);

        return ResponseEntity.created(URI.create("/reservations/" + id)).body(reservationResponse);
    }

    @GetMapping("/reservations/waiting")
    public ResponseEntity<List<WaitingResponse>> findWaiting() {
        List<WaitingResponse> waitingResponses = reservationService.findWaiting();

        return ResponseEntity.ok(waitingResponses);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/me")
    public ResponseEntity<List<MyReservationResponse>> myReservations(@Login LoginMemberInToken loginMemberInToken) {
        List<MyReservationResponse> myReservationResponses = reservationService.findAllByMemberId(
                loginMemberInToken.id());

        return ResponseEntity.ok(myReservationResponses);
    }
}
