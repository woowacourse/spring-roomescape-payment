package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import roomescape.auth.core.AuthenticationPrincipal;
import roomescape.auth.domain.AuthInfo;
import roomescape.waiting.dto.request.CreateWaitingRequest;
import roomescape.waiting.dto.response.CreateWaitingResponse;
import roomescape.waiting.dto.response.FindWaitingWithRankingResponse;
import roomescape.waiting.service.WaitingService;

@Tag(name = "예약 대기 API", description = "예약 대기 관련 API")
@RestController
@RequestMapping
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약 대기 생성 API")
    @PostMapping("/waitings")
    public ResponseEntity<CreateWaitingResponse> createWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                                               @Valid @RequestBody CreateWaitingRequest createWaitingRequest) {
        CreateWaitingResponse createWaitingResponse = waitingService.createWaiting(authInfo, createWaitingRequest);
        return ResponseEntity.created(URI.create("/waitings/" + createWaitingResponse.waitingId()))
                .body(createWaitingResponse);
    }

    @Operation(summary = "예약 대기 삭제 API")
    @DeleteMapping("/waitings/{waitingId}")
    public ResponseEntity<Void> deleteWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                                               @PathVariable Long waitingId) {
        waitingService.deleteWaiting(authInfo, waitingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 예약 대기 목록 조회 API")
    @GetMapping("/members/waitings")
    public ResponseEntity<List<FindWaitingWithRankingResponse>> getWaitingsByMember(
            @AuthenticationPrincipal AuthInfo authInfo) {
        return ResponseEntity.ok(waitingService.getWaitingsByMember(authInfo));
    }
}
