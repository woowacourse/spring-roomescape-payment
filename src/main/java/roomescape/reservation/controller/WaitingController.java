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
import roomescape.reservation.service.WaitingFacadeService;

@RestController
public class WaitingController {

    private final WaitingFacadeService waitingFacadeService;

    public WaitingController(WaitingFacadeService waitingFacadeService) {
        this.waitingFacadeService = waitingFacadeService;
    }


    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> findWaitings(
    ) {
        return ResponseEntity.ok(waitingFacadeService.findWaitings());
    }

    @RequireRole(MemberRole.USER)
    @DeleteMapping("/waiting/{id}")
    public ResponseEntity<Void> deleteReservations(
            @PathVariable("id") Long id
    ) {
        waitingFacadeService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @RequireRole(MemberRole.USER)
    @PostMapping("/waiting")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            UserInfo userInfo
    ) {
        ReservationResponse response = waitingFacadeService.createWaiting(request, userInfo.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
