package roomescape.reservation.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.reservation.waiting.dto.response.WaitingResponse;
import roomescape.reservation.waiting.service.WaitingService;

@Tag(name = "관리자 예약 대기", description = "관리자 예약 대기 API")
@Slf4j
@RequestMapping("/admin/waitings")
@RestController
public class AdminWaitingController {

    private final WaitingService waitingService;

    public AdminWaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "전체 대기 목록 조회", description = "관리자가 전체 대기 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "전체 대기 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json")
    )
    @RequiredAdmin
    @GetMapping
    public ResponseEntity<List<WaitingResponse>> readAllWaiting() {
        List<WaitingResponse> responses = waitingService.getAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "대기 거절", description = "관리자가 특정 대기 요청을 거절(삭제)합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "대기 삭제 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 대기 ID",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @RequiredAdmin
    @DeleteMapping("/{waitingId}")
    public ResponseEntity<Void> deny(
            @Parameter(description = "삭제할 대기 ID", example = "1")
            @PathVariable("waitingId") final Long waitingId
    ) {
        log.info("대기 삭제 요청: waitingId={}", waitingId);
        waitingService.deleteWaiting(waitingId);
        return ResponseEntity.noContent().build();
    }
}
