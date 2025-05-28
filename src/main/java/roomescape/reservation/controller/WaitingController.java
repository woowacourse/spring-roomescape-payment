package roomescape.reservation.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RequireRole;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationFacadeService;
import roomescape.reservation.service.WaitingService;

@RestController
public class WaitingController {

    private final WaitingService waitingService;
    private final ReservationFacadeService reservationFacadeService;

    public WaitingController(final WaitingService waitingService,
                             final ReservationFacadeService reservationFacadeService) {
        this.waitingService = waitingService;
        this.reservationFacadeService = reservationFacadeService;
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> findWaitings(
    ) {
        return ResponseEntity.ok(waitingService.findWaitings());
    }

    @RequireRole(MemberRole.USER)
    @DeleteMapping("/waiting/{id}")
    public ResponseEntity<Void> deleteReservations(
            @PathVariable("id") Long id
    ) {
        waitingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequireRole(MemberRole.USER)
    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            UserInfo userInfo
    ) {
        ReservationResponse dto = reservationFacadeService.createWaiting(request.date(), request.timeId(),
                request.themeId(), userInfo.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
