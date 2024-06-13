package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.Login;
import roomescape.service.WaitingService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.request.WaitingRequest;
import roomescape.service.dto.response.WaitingResponse;

@Tag(name = "[USER] 예약대기 API", description = "사용자가 예약 대기를 생성/삭제할 수 있습니다.")
@RestController
public class WaitingController {
    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약대기 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "생성된 예약 대기 정보를 반환합니다.")
    })
    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> saveWaiting(
            @Login LoginMember member,
            @RequestBody @Valid WaitingRequest waitingRequest
    ) {
        WaitingResponse waitingResponse = waitingService.saveWaiting(waitingRequest, member.id());
        return ResponseEntity.ok(waitingResponse);
    }

    @Operation(summary = "예약대기 삭제 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204", description = "예약대기 삭제에 성공했습니다.")
    })
    @DeleteMapping("/waitings")
    public ResponseEntity<Void> deleteWaiting(
            @Login LoginMember member,
            @RequestParam(name = "id") long id) {
        waitingService.deleteUserWaiting(id, member.id());
        return ResponseEntity.noContent().build();
    }
}
