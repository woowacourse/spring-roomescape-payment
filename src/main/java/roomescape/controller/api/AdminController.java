package roomescape.controller.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.AdminReservationRequest;
import roomescape.controller.dto.request.WaitingToReservationRequest;
import roomescape.controller.dto.response.ApiResponses;
import roomescape.controller.support.Auth;
import roomescape.security.authentication.Authentication;
import roomescape.service.ReservationManageService;
import roomescape.service.ReservationWaitingService;
import roomescape.service.dto.request.WaitingApproveRequest;
import roomescape.service.dto.response.ReservationResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ReservationManageService reservationManageService;
    private final ReservationWaitingService reservationWaitingService;

    public AdminController(ReservationManageService reservationManageService, ReservationWaitingService reservationWaitingService) {
        this.reservationManageService = reservationManageService;
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addAdminReservation(@RequestBody @Valid AdminReservationRequest request) {
        ReservationResponse response = reservationManageService.addReservationByAdmin(request.toAdminReservationCreateRequest());
        return ResponseEntity.created(URI.create("/reservation/" + response.id()))
                .body(response);
    }

    @PostMapping("/waitings/{id}")
    public ResponseEntity<ReservationResponse> approveReservationWaiting(@PathVariable long id,
                                                                         @Valid @RequestBody WaitingToReservationRequest request,
                                                                         @Auth Authentication authentication) {

        WaitingApproveRequest waitingApproveRequest = request.toWaitingApproveRequest(id, authentication.getPrincipal());
        ReservationResponse response = reservationWaitingService.approveReservationWaiting(waitingApproveRequest);
        return ResponseEntity.created(URI.create("/reservation/" + response.id()))
                .body(response);
    }

    @GetMapping("/waitings")
    public ApiResponses<ReservationResponse> getReservationWaitings() {
        List<ReservationResponse> reservationWaitings = reservationWaitingService.getReservationWaitings();
        return new ApiResponses<>(reservationWaitings);
    }

    @DeleteMapping("/waitings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationWaiting(@PathVariable long id, @Auth Authentication authentication) {
        reservationWaitingService.deleteReservationWaiting(id, authentication.getPrincipal());
    }
}
