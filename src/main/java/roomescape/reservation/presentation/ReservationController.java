package roomescape.reservation.presentation;

import static roomescape.reservation.presentation.ReservationController.RESERVATION_BASE_URL;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.argumentResolver.Login;
import roomescape.common.exceptionHandler.dto.ExceptionResponse;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.client.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.client.dto.response.TossPaymentResponse;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping(RESERVATION_BASE_URL)
public class ReservationController {

    public static final String RESERVATION_BASE_URL = "/reservations";
    private static final String SLASH = "/";

    private final ReservationService reservationService;
    private final TossPaymentClient tossPaymentClient;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService, TossPaymentClient tossPaymentClient, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.tossPaymentClient = tossPaymentClient;
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute ReservationConditionRequest request) {
        List<ReservationResponse> response = reservationService.getReservations(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody final ReservationRequest request,
            @Login final LoginMember loginMember
    ) {

        TossPaymentConfirmRequest confirmRequest = new TossPaymentConfirmRequest(
                request.orderId(),
                request.amount(),
                request.paymentKey()
        );

        TossPaymentResponse tossPaymentResponse = tossPaymentClient.confirmPayment(confirmRequest);
        ReservationResponse response = reservationService.createReservation(request, loginMember.id());
        paymentService.save(tossPaymentResponse, response.id());

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

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> getMyReservations(@Login LoginMember loginMember) {
        List<MyReservationResponse> myReservationResponses = reservationService.getMyReservations(loginMember.id());
        return ResponseEntity.ok().body(myReservationResponses);
    }
}
