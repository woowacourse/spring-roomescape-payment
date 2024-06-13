package roomescape.registration.domain.waiting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.registration.domain.waiting.dto.WaitingRequest;
import roomescape.registration.domain.waiting.dto.WaitingResponse;
import roomescape.registration.domain.waiting.service.WaitingService;

import java.util.List;

@RestController
public class WaitingController implements WaitingControllerSwagger {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Override
    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> waitingSave(@RequestBody WaitingRequest waitingRequest,
                                                       @LoginMemberId long memberId) {
        WaitingResponse waiting = waitingService.addWaiting(waitingRequest, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(waiting);
    }

    @Override
    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> waitingList() {
        List<WaitingResponse> waitings = waitingService.findWaitings();

        return ResponseEntity.ok(waitings);
    }

    @Override
    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<WaitingResponse> waitingRemove(@PathVariable long waitingId, @LoginMemberId Long memberId) {
        waitingService.removeWaiting(waitingId, memberId);

        return ResponseEntity.noContent().build();
    }
}
