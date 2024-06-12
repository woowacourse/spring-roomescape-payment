package roomescape.presentation.reservation;

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
import roomescape.application.reservation.ReservationLookupService;
import roomescape.application.reservation.ReservationService;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationStatusResponse;
import roomescape.presentation.auth.LoginMemberId;
import roomescape.presentation.auth.PermissionRequired;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationLookupService reservationLookupService;

    public ReservationController(ReservationService reservationService,
                                 ReservationLookupService reservationLookupService) {
        this.reservationService = reservationService;
        this.reservationLookupService = reservationLookupService;
    }

    @GetMapping
    @PermissionRequired
    public ResponseEntity<List<ReservationResponse>> findBookedReservations() {
        List<ReservationResponse> responses = reservationLookupService.findAllBookedReservations();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationStatusResponse>> findMyReservations(@LoginMemberId long memberId) {
        List<ReservationStatusResponse> responses = reservationLookupService.getReservationStatusesByMemberId(memberId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@LoginMemberId long memberId,
                                                      @RequestBody @Valid ReservationPaymentRequest request) {
        ReservationResponse response = reservationService.createReservation(request.withMemberId(memberId));
        URI location = URI.create("/reservations/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginMemberId long memberId, @PathVariable long id) {
        reservationService.cancelReservation(memberId, id);
        return ResponseEntity.noContent().build();
    }
}
