package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
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

import java.util.List;

@Tag(name = "관리자 예약 대기 API", description = "관리자 예약 대기 관련 API")
@RestController
@RequestMapping("/admin/waitings")
public class AdminWaitingController {
    private final WaitingService waitingService;

    public AdminWaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "관리자 예약 대기 목록 조회 API")
    @ApiResponse(responseCode = "200", description = "관리자 예약 대기 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<FindWaitingResponse>> getWaitings() {
        return ResponseEntity.ok(waitingService.getWaitings());
    }

    @Operation(summary = "관리자 예약 대기 거절 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "관리자 예약 대기 거절 성공"),
            @ApiResponse(responseCode = "403", description = "회원의 권한이 없어, 식별자 memberId인 예약 대기를 삭제할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 예약 대기가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @DeleteMapping("/reject/{waitingId}")
    public ResponseEntity<Void> rejectWaiting(@AuthenticationPrincipal AuthInfo authInfo,
                                              @PathVariable Long waitingId) {
        waitingService.deleteWaiting(authInfo, waitingId);
        return ResponseEntity.noContent().build();
    }
}
