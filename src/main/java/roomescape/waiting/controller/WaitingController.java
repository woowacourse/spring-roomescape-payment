package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.auth.service.dto.LoginMember;
import roomescape.waiting.service.WaitingService;
import roomescape.waiting.service.dto.request.CreateWaitingRequest;
import roomescape.waiting.service.dto.response.CreateWaitingResponse;
import roomescape.waiting.service.dto.response.WaitingInfoResponse;

import java.util.List;

@RestController
@RequestMapping("/waiting")
@Tag(name = "예약 대기 컨트롤러", description = "예약 대기에 관한 API 모음")
public class WaitingController {

    private static final Logger log = LoggerFactory.getLogger(WaitingController.class);

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @RequiredAdmin
    @GetMapping
    @Operation(summary = "예약 대기 목록 조회", description = "어드민은 모든 예약 대기 목록을 조회합니다.")
    public ResponseEntity<List<WaitingInfoResponse>> getAll() {
        List<WaitingInfoResponse> waitings = waitingService.findAll();
        return ResponseEntity.ok(waitings);
    }

    @PostMapping
    @Operation(summary = "예약 대기 생성", description = "선택 날짜,테마,시간에 대한 예약 대기를 생성합니다.")
    public ResponseEntity<CreateWaitingResponse> create(
            LoginMember loginMember,
            @Valid @RequestBody CreateWaitingRequest request
    ) {
        log.info(
                "[POST /WaitingCreate.Request] date={},themeId={},timeId={}",
                request.date(),
                request.themeId(),
                request.timeId()
                );

        CreateWaitingResponse response = waitingService.createWaiting(request, loginMember);

        log.info(
                "[POST / WaitingCreate.response] [SUCCESS] date={}, time={}, theme={}",
                response.date(),
                response.time(),
                response.theme()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 대기 삭제", description = "선택한 예약 대기를 삭제합니다.")
    public ResponseEntity<Void> delete(LoginMember loginMember, @PathVariable("id") Long id) {
        log.info("[DELETE /WaitingDELTE.Request] id={},loginMember={}", id, loginMember);

        waitingService.delete(id, loginMember);

        log.info("[DELETE /WaitingDELETE] [SUCCESS]");
        return ResponseEntity.noContent().build();
    }
}
