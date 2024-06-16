package roomescape.admin.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.admin.dto.ReservationFilterRequest;
import roomescape.auth.annotation.Auth;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.dto.ReservationDto;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.service.WaitingService;

@RestController
@Auth(roles = MemberRole.ADMIN)
@RequestMapping("/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final WaitingService waitingService;

    public AdminController(ReservationService reservationService,
                           WaitingService waitingService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Void> reservationSave(@RequestBody AdminReservationRequest adminReservationRequest) {
        reservationService.addAdminReservation(adminReservationRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> reservationFilteredList(
            @ModelAttribute ReservationFilterRequest reservationFilterRequest) {
        List<ReservationDto> reservationDto = reservationService.findFilteredReservations(reservationFilterRequest);

        return reservationDto.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/waitings")
    public List<WaitingResponse> waitingList() {
        return waitingService.findWaitings();
    }

    @DeleteMapping("/waitings/{waitingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void waitingReject(@PathVariable long waitingId) {
        waitingService.removeWaiting(waitingId);
    }
}
