package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.waiting.WaitingCommandService;
import roomescape.service.waiting.WaitingQueryService;
import roomescape.service.waiting.dto.WaitingRequest;

@RestController
@RequestMapping("/waitings")
public class WaitingController {

    private final WaitingQueryService waitingQueryService;
    private final WaitingCommandService waitingCommandService;

    public WaitingController(WaitingQueryService waitingQueryService, WaitingCommandService waitingCommandService) {
        this.waitingQueryService = waitingQueryService;
        this.waitingCommandService = waitingCommandService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createWaiting(
        @RequestBody WaitingRequest waitingRequest,
        @LoginMemberId Long memberId) {
        ReservationResponse response = waitingCommandService.createWaiting(waitingRequest, memberId);
        return ResponseEntity.created(URI.create("/waitings/" + response.id())).body(response);
    }

    @GetMapping
    public List<ReservationResponse> findAll() {
        return waitingQueryService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") long reservationId, @LoginMemberId long memberId) {
        waitingCommandService.deleteWaitingById(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }
}
