package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        CreateWaitingResponse response = waitingService.createWaiting(request, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 대기 삭제", description = "선택한 예약 대기를 삭제합니다.")
    public ResponseEntity<Void> delete(LoginMember loginMember, @PathVariable("id") Long id) {
        waitingService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
