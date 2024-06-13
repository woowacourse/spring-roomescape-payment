package roomescape.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.ReservationRequest;
import roomescape.controller.dto.request.ReservationWaitingRequest;
import roomescape.controller.dto.response.ApiResponses;
import roomescape.controller.support.Auth;
import roomescape.security.authentication.Authentication;
import roomescape.service.ReservationWaitingService;
import roomescape.service.dto.response.ReservationResponse;

@RestController
@Validated
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping("/waitings")
    public ResponseEntity<ReservationResponse> createReservationWaiting(@Valid @RequestBody ReservationWaitingRequest request,
                                                                        @Auth Authentication authentication) {
        long memberId = authentication.getId();
        ReservationResponse response = reservationWaitingService.addReservationWaiting(
                request.toCreateReservationRequest(memberId));
        URI location = URI.create("/waitings/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/waitings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationWaiting(@PathVariable @Positive long id, @Auth Authentication authentication) {
        long memberId = authentication.getId();
        reservationWaitingService.deleteReservationWaiting(id, memberId);
    }

    @GetMapping("/admin/waitings")
    public ApiResponses<ReservationResponse> getReservationWaitings() {
        List<ReservationResponse> reservationWaitings = reservationWaitingService.getReservationWaitings();
        return new ApiResponses<>(reservationWaitings);
    }
}
