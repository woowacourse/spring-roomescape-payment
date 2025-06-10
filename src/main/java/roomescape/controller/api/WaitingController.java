package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.auth.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.waiting.MemberWaitingCreateRequest;
import roomescape.dto.waiting.WaitingCreateRequest;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.service.WaitingService;

@Tag(name = "예약 대기 API", description = "예약 대기 관련 API입니다.")
@RestController
@RequestMapping("/waitings")
public class WaitingController {
    private static final Logger log = LoggerFactory.getLogger(WaitingController.class);
    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "전체 대기 목록 조회", description = "모든 대기 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WaitingResponse>> getAllWaitings() {
        return ResponseEntity.ok().body(waitingService.findAllWaitings());
    }

    @Operation(summary = "예약 대기 등록", description = "로그인한 사용자가 대기 요청을 등록합니다.")
    @PostMapping
    public ResponseEntity<WaitingResponse> addWaiting(
            @Parameter(hidden = true) @CurrentMember LoginInfo loginInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "대기 요청 정보")
            @RequestBody final MemberWaitingCreateRequest request) {
        WaitingCreateRequest waitingCreateRequest = new WaitingCreateRequest(
                request.date(), loginInfo.id(), request.themeId(), request.timeId());
        WaitingResponse response = waitingService.createWaiting(waitingCreateRequest);
        log.info("Waiting added: memberId={}, themeId={}, date={}, timeId={}",
                loginInfo.id(), request.themeId(), request.date(), request.timeId());
        return ResponseEntity.created(URI.create("waitings/" + response.id())).body(response);
    }

    @Operation(summary = "대기 승인", description = "예약 대기 상태를 승인 처리합니다.")
    @PostMapping("/{id}")
    public ResponseEntity<Void> approveWaiting(
            @Parameter(description = "대기 ID") @PathVariable("id") final Long id) {
        waitingService.approveWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "대기 삭제", description = "특정 대기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(
            @Parameter(description = "삭제할 대기 ID") @PathVariable("id") final Long id) {
        waitingService.deleteWaiting(id);
        log.info("Waiting deleted: id={}", id);
        return ResponseEntity.noContent().build();
    }
}
