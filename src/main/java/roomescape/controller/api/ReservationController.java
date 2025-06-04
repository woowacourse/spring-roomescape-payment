package roomescape.controller.api;

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
import roomescape.dto.auth.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.reservation.ReservationPaymentRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.ReservationPaymentService;
import roomescape.service.ReservationService;
import roomescape.dto.reservation.ReservationCreateRequest;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationPaymentService reservationPaymentService;

    public ReservationController(ReservationService reservationService,
                                 ReservationPaymentService reservationPaymentService) {
        this.reservationService = reservationService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> allReservations = reservationService.findAllReservationResponses();
        return ResponseEntity.ok(allReservations);
    }

    @PostMapping
    public ResponseEntity<String> addReservation(@CurrentMember LoginInfo loginInfo,
                                                 @RequestBody final ReservationPaymentRequest request) {
        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(request.date(),
                request.themeId(),
                request.timeId(),
                loginInfo.id());

        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(request.orderId(), request.amount(),
                request.paymentKey(), request.paymentType());

        ReservationResponse response = reservationPaymentService.confirmPaymentAndAddReservation(
                reservationCreateRequest,
                paymentConfirmRequest);

        return ResponseEntity.created(URI.create("reservations/" + response.id())).body("성공했습니다.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
