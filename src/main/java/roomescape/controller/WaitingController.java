package roomescape.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.UserWaitingSaveRequest;
import roomescape.infrastructure.Login;
import roomescape.service.ReservationService;
import roomescape.service.dto.LoginMember;
import roomescape.service.dto.ReservationRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationStatus;

@RestController
@RequestMapping("/reservations/waiting")
public class WaitingController {

    private final ReservationService reservationService;

    public WaitingController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> saveWaiting(
            @Login LoginMember member,
            @RequestBody @Valid UserWaitingSaveRequest userWaitingSaveRequest
    ) {
        ReservationRequest reservationRequest = userWaitingSaveRequest.toReservationRequest(member.id());
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationRequest);

        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            return ResponseEntity.created(URI.create("/reservations/booked/" + reservationResponse.id()))
                    .body(reservationResponse);
        }
        return ResponseEntity.created(URI.create("/reservations/waiting/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelWaiting(@Login LoginMember member, @PathVariable Long id) {
        reservationService.cancelWaiting(id, member);
        return ResponseEntity.noContent().build();
    }
}
