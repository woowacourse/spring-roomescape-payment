package roomescape.presentation;

import jakarta.validation.Valid;
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
import roomescape.auth.Authenticated;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.ReservationForMemberResponse;
import roomescape.dto.response.ReservationWithStatusResponse;
import roomescape.service.ReservationFacade;
import roomescape.service.ReservationService;

@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationFacade reservationFacade;

    public ReservationController(final ReservationService reservationService,
                                 final ReservationFacade reservationFacade) {
        this.reservationService = reservationService;
        this.reservationFacade = reservationFacade;
    }

    @PostMapping
    public ResponseEntity<ReservationForMemberResponse> createNewReservation(
            @Authenticated Long memberId,
            @Valid @RequestBody ReservationCreateRequest request) {
        PaymentRequest paymentRequest = new PaymentRequest(request.amount(), request.paymentKey(), request.orderId());

        ReservationForMemberResponse reservationResponse = reservationFacade.processReservationForMember(
                memberId, request.timeId(), request.themeId(), request.date(), paymentRequest
        );

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
