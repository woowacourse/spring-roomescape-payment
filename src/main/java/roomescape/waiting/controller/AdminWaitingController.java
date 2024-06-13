package roomescape.waiting.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.core.AuthenticationPrincipal;
import roomescape.auth.domain.AuthInfo;
import roomescape.waiting.dto.response.FindWaitingResponse;
import roomescape.waiting.service.WaitingService;

@RestController
@RequestMapping("/admin/waitings")
public class AdminWaitingController extends AdminWaitingControllerApi {
    private final WaitingService waitingService;

    public AdminWaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @GetMapping
    public ResponseEntity<List<FindWaitingResponse>> getWaitings() {
        return ResponseEntity.ok(waitingService.getWaitings());
    }

    @DeleteMapping("/reject/{waitingId}")
    public ResponseEntity<Void> rejectWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                              @PathVariable Long waitingId) {
        waitingService.deleteWaiting(authInfo, waitingId);
        return ResponseEntity.noContent().build();
    }
}
