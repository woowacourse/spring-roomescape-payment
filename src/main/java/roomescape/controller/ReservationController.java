package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
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
import roomescape.config.LoginMemberConverter;
import roomescape.domain.reservation.Status;
import roomescape.dto.LoginMember;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.request.reservation.WaitingRequest;
import roomescape.dto.response.reservation.MyReservationResponse;
import roomescape.dto.response.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;
import roomescape.service.ReservationService;
import roomescape.service.TossPaymentService;

@RestController
public class ReservationController {
    private final RestClient restClient;
    private final ReservationService reservationService;
    private final TossPaymentService tossPaymentService;

    public ReservationController(ReservationService reservationService, TossPaymentService tossPaymentService) {
        this.tossPaymentService = tossPaymentService;
        this.restClient = RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments/confirm").build();
        this.reservationService = reservationService;
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
        restClient.post().header("Authorization", tossPaymentService.createAuthorization())
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossPaymentService.createPaymentRequest(reservationRequest))
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    throw new RoomescapeException(HttpStatus.NOT_FOUND, "결제 승인 안됨");
                }));
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
