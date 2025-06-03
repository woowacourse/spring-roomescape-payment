package roomescape.presentation;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.Authenticated;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWithStatusResponse;
import roomescape.service.PaymentClient;
import roomescape.service.ReservationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentClient paymentClient;

    public ReservationController(final ReservationService reservationService,
                                 final PaymentClient paymentClient) {
        this.reservationService = reservationService;
        this.paymentClient = paymentClient;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createNewReservation(
            @Authenticated Long memberId,
            @Valid @RequestBody ReservationCreateRequest request) {
        PaymentRequest paymentRequest = new PaymentRequest(request.amount(), request.paymentKey(), request.orderId());
        PaymentInfo paymentInfo = paymentClient.postPaymentInfo(paymentRequest);

        ReservationResponse reservationResponse = reservationService.createReservationForMember(
                memberId, request.timeId(), request.themeId(), request.date());

        return ResponseEntity
                .created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public List<ReservationWithStatusResponse> getMyBookingHistory(@Authenticated Long id) {
        return reservationService.findBookingHistory(id);
    }
}
