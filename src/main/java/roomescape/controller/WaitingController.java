package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.Login;
import roomescape.service.WaitingService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.request.WaitingRequest;
import roomescape.service.dto.response.WaitingResponse;

@RestController
public class WaitingController {
    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약대기 API", description = "예약 대기를 생성한다.")
    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> saveWaiting(
            @Login LoginMember member,
            @RequestBody @Valid WaitingRequest waitingRequest
    ) {
        WaitingResponse waitingResponse = waitingService.saveWaiting(waitingRequest, member.id());
        return ResponseEntity.ok(waitingResponse);
    }

    @Operation(summary = "예약대기 삭제 API", description = "예약 대기를 삭제한다.")
    @DeleteMapping("/waitings")
    public ResponseEntity<Void> deleteWaiting(
            @Login LoginMember member,
            @RequestParam(name = "id") long id) {
        waitingService.deleteUserWaiting(id, member.id());
        return ResponseEntity.noContent().build();
    }
}
