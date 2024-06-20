package roomescape.controller.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.auth.AuthenticationPrincipal;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWaitingRequest;
import roomescape.dto.reservation.ReservationWithPaymentRequest;
import roomescape.service.WaitingService;

@RequestMapping("/waitings")
@RestController
public class WaitingController implements WaitingControllerDocs {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservationWaiting(
            @AuthenticationPrincipal final LoginMember loginMember,
            @RequestBody final ReservationWithPaymentRequest request
    ) {
        final ReservationWaitingRequest waitingRequest = new ReservationWaitingRequest(request, loginMember.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(waitingService.createReservationWaiting(waitingRequest));
    }
}
