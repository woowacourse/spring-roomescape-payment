package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.WaitingService;
import roomescape.service.dto.response.WaitingResponses;

@Tag(name = "[ADMIN] 예약대기 API", description = "어드민 권한으로 예약대기를 조회/삭제할 수 있습니다.")
@RestController
public class AdminWaitingController {
    private final WaitingService waitingService;

    public AdminWaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "어드민 예약대기 조회 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "전체 예약 대기 정보를 반환합니다.")
    })
    @GetMapping("/admin/waitings/all")
    ResponseEntity<WaitingResponses> allWaitings() {
        WaitingResponses allWaitings = waitingService.findAllWaitings();
        return ResponseEntity.ok(allWaitings);
    }

    @Operation(summary = "어드민 예약대기 삭제 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "204", description = "예약 대기 삭제에 성공했습니다.")
    })
    @DeleteMapping("/admin/waitings")
    public ResponseEntity<Void> deleteWaiting(@RequestParam(name = "id") long id) {
        waitingService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
