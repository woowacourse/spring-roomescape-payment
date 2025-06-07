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

@Tag(name = "예약 대기")
@RestController
@RequestMapping("/waiting")
public class WaitingController {
    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약 대기 취소", description = "로그인 유저의 예약 대기를 삭제한다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(LoginMember loginMember, @PathVariable("id") Long id) {
        waitingService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 대기 생성", description = "로그인 유저의 예약 대기를 생성한다.")
    @PostMapping
    public ResponseEntity<CreateWaitingResponse> create(
            LoginMember loginMember,
            @Valid @RequestBody CreateWaitingRequest request
    ) {
        CreateWaitingResponse response = waitingService.createWaiting(request, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "전체 예약 대기 조회", description = "모든 예약 대기 항목들을 조회한다.")
    @RequiredAdmin
    @GetMapping
    public ResponseEntity<List<WaitingInfoResponse>> getAll() {
        List<WaitingInfoResponse> waitings = waitingService.findAll();
        return ResponseEntity.ok(waitings);
    }
}
