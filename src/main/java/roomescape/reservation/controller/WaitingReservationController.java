package roomescape.reservation.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.service.WaitingReservationService;

import java.net.URI;

@RestController
@RequestMapping("/reservations/waiting")
public class WaitingReservationController {

    private final WaitingReservationService waitingReservationService;

    public WaitingReservationController(WaitingReservationService waitingReservationService) {
        this.waitingReservationService = waitingReservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserveWaiting(
            @LoginUser AuthInfo authInfo,
            @RequestBody @Valid ReservationRequest reservationRequest) {
        ReservationResponse response = waitingReservationService.reserveWaiting(reservationRequest, authInfo.getId());
        return ResponseEntity.created(URI.create("/reservations/" + response.reservationId())).body(response);
    }
}
