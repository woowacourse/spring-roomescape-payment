package roomescape.reservation.presentation;

import static roomescape.reservation.presentation.ReservationController.RESERVATION_BASE_URL;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import roomescape.common.argumentResolver.Login;
import roomescape.common.exceptionHandler.dto.ExceptionResponse;
import roomescape.member.dto.request.LoginMember;
import roomescape.reservation.dto.request.PaymentConfirmRequest;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping(RESERVATION_BASE_URL)
public class ReservationController {

    public static final String RESERVATION_BASE_URL = "/reservations";
    public static final String BASE_URL = "https://api.tosspayments.com";
    private static final String SLASH = "/";

    private final ReservationService reservationService;
    private final RestClient restClient;

    public ReservationController(ReservationService reservationService, RestTemplateBuilder restTemplateBuilder) {
        this.reservationService = reservationService;
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute ReservationConditionRequest request) {
        List<ReservationResponse> response = reservationService.getReservations(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final ReservationRequest request,
                                                                 @Login final LoginMember loginMember) {
        PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest(request.orderId(), request.amount(), request.paymentKey());
        restClient.post()
                .uri("/v1/payments/confirm")
                .body(confirmRequest)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (res, req) -> {
                    throw new IllegalArgumentException(res.toString());
                })
                .toBodilessEntity();

        ReservationResponse response = reservationService.createReservation(request, loginMember.id());
        URI locationUri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable("id") final Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> noMatchDateType(final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                "[ERROR] 요청 날짜 형식이 맞지 않습니다.", request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(exceptionResponse);
    }
    // TODO : URL
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> getMyReservations(@Login LoginMember loginMember) {
        List<MyReservationResponse> myReservationResponses = reservationService.getMyReservations(loginMember.id());
        return ResponseEntity.ok().body(myReservationResponses);
    }
}
