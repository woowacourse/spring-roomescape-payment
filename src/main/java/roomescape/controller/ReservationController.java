package roomescape.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import roomescape.dto.response.ErrorResponse;
import roomescape.dto.response.PaymentRequest;
import roomescape.exception.RoomescapeException;
import roomescape.service.ReservationService;
import roomescape.config.LoginMemberConverter;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.response.reservation.ReservationResponse;

@RestController
public class ReservationController {
    private final RestClient restClient;
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.restClient = RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments/confirm").build();
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findAllByReservation() {
        List<ReservationResponse> responses = reservationService.findAllByStatus(Status.RESERVATION);
        return ResponseEntity.ok(responses);
    }

    @PostMapping(value = {"/reservations", "/waitings"})
    public ResponseEntity<ReservationResponse> saveReservationByClient(
            @LoginMemberConverter LoginMember loginMember,
            @RequestBody ReservationRequest reservationRequest) {
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        PaymentRequest paymentRequest = new PaymentRequest(reservationRequest.orderId(), reservationRequest.amount(),
                reservationRequest.paymentKey());
        restClient.post().header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RoomescapeException(HttpStatus.NOT_FOUND, "결제 승인 안됨");
                }));
        ReservationResponse response = reservationService.saveByClient(loginMember, reservationRequest);
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
