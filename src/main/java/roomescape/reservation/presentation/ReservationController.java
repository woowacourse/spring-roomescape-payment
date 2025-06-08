package roomescape.reservation.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.member.docs.LoginMemberDocs;
import roomescape.payment.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.adaptor.ReservationApiAdaptor;
import roomescape.reservation.docs.MyReservationAndWaitingResponseDocs;
import roomescape.reservation.docs.ReservationConditionRequestDocs;
import roomescape.reservation.docs.ReservationRequestDocs;
import roomescape.reservation.docs.ReservationResponseDocs;
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
    private final ReservationApiAdaptor reservationApiAdaptor;

    public ReservationController(final ReservationService reservationService, final PaymentService paymentService, final ReservationApiAdaptor reservationApiAdaptor) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.reservationApiAdaptor = reservationApiAdaptor;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDocs>> getReservations(
            @ModelAttribute final ReservationConditionRequestDocs requestDocs) {
        ReservationConditionRequest request = reservationApiAdaptor.toReservationConditionRequest(requestDocs);

        List<ReservationResponse> response = reservationService.getReservations(request);
        List<ReservationResponseDocs> reservationResponseDocs = response.stream().map(reservationApiAdaptor::toReservationResponseDocs).toList();
        return ResponseEntity.ok(reservationResponseDocs);
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDocs> createReservation(
            @RequestBody final ReservationRequestDocs requestDocs,
            @Login final LoginMemberDocs loginMember
    ) {
        ReservationRequest request = reservationApiAdaptor.toReservationRequest(requestDocs);

        ReservationResponse response = reservationService.createPendingReservation(request, loginMember.id());
        TossPaymentConfirmRequest tossPaymentConfirmRequest = new TossPaymentConfirmRequest(request.orderId(), request.amount(), request.paymentKey());
        paymentService.confirmAndSavePayment(tossPaymentConfirmRequest, response.id());

        ReservationResponseDocs reservationResponseDocs = reservationApiAdaptor.toReservationResponseDocs(response);

        URI locationUri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(reservationResponseDocs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable("id") final Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationAndWaitingResponseDocs>> getMyReservations(@Login LoginMemberDocs loginMember) {
        List<MyReservationAndWaitingResponse> myReservationResponses = reservationService.getMyReservations(loginMember.id());

        List<MyReservationAndWaitingResponseDocs> myReservationResponseDocs = myReservationResponses.stream().map(reservationApiAdaptor::toMyReservationAndWaitingResponseDocs).toList();
        return ResponseEntity.ok().body(myReservationResponseDocs);
    }
}
