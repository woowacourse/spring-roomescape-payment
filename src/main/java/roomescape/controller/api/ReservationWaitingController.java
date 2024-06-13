package roomescape.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.WaitingRequest;
import roomescape.controller.support.Auth;
import roomescape.security.authentication.Authentication;
import roomescape.service.ReservationWaitingService;
import roomescape.service.dto.response.ReservationResponse;

import java.net.URI;

@RestController
@RequestMapping("/waitings")
@Validated
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservationWaiting(@Valid @RequestBody WaitingRequest request,
                                                                        @Auth Authentication authentication) {
        ReservationResponse response = reservationWaitingService.addReservationWaiting(
                request.toWaitingCreateRequest(authentication.getPrincipal()));
        URI location = URI.create("/waitings/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationWaiting(@Positive @PathVariable long id, @Auth Authentication authentication) {
        reservationWaitingService.deleteReservationWaiting(id, authentication.getPrincipal());
    }
}
