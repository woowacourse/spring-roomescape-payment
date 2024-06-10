package roomescape.waiting.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "관리자 예약 대기 API", description = "관리자 예약 대기 관련 API")
@RestController
@RequestMapping("/admin/waitings")
public class AdminWaitingController {
    private final WaitingService waitingService;

    public AdminWaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "관리자 예약 대기 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<FindWaitingResponse>> getWaitings() {
        return ResponseEntity.ok(waitingService.getWaitings());
    }

    @Operation(summary = "관리자 예약 대기 거절 API")
    @DeleteMapping("/reject/{waitingId}")
    public ResponseEntity<Void> rejectWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                              @PathVariable Long waitingId) {
        waitingService.deleteWaiting(authInfo, waitingId);
        return ResponseEntity.noContent().build();
    }
}
