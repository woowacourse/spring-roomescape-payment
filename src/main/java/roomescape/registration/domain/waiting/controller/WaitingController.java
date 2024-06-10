package roomescape.registration.domain.waiting.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

@Tag(name = "예약 대기 컨트롤러", description = "사용자의 요청을 받아 예약 대기 신청, 예약 대기 삭제 등을 수행한다.")
@RestController
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> waitingSave(@RequestBody WaitingRequest waitingRequest,
                                                       @LoginMemberId long memberId) {
        WaitingResponse waiting = waitingService.addWaiting(waitingRequest, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(waiting);
    }

    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> waitingList() {
        List<WaitingResponse> waitings = waitingService.findWaitings();

        return ResponseEntity.ok(waitings);
    }

    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<WaitingResponse> waitingRemove(@PathVariable long waitingId) {
        waitingService.removeWaiting(waitingId);

        return ResponseEntity.noContent().build();
    }
}
