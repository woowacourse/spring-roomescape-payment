package roomescape.reservation.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.login.dto.Accessor;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<List<MemberReservationResponse>> findMemberReservation(
            @Authenticated Accessor accessor) {
        return ResponseEntity.ok(reservationService.findMemberReservationWithWaitingStatus(accessor.id()));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveMemberReservation(
            @Authenticated Accessor accessor,
            @Valid @RequestBody MemberReservationAddRequest memberReservationAddRequest) {
        ReservationResponse saveResponse = reservationService.saveMemberReservation(accessor.id(),
                memberReservationAddRequest);
        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable("id") Long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.noContent().build();
    }
}
