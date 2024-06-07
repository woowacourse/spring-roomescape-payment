package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.service.WaitingService;
import roomescape.service.dto.response.WaitingResponses;

@RestController
public class AdminWaitingController {
    private final WaitingService waitingService;

    public AdminWaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "어드민 예약대기 조회 API", description = "어드민이 예약대기를 조회한다.")
    @GetMapping("/admin/waitings/all")
    ResponseEntity<WaitingResponses> allWaitings() {
        WaitingResponses allWaitings = waitingService.findAllWaitings();
        return ResponseEntity.ok(allWaitings);
    }

    @Operation(summary = "어드민 예약대기 삭제 API", description = "어드민이 예약대기를 삭제한다.")
    @DeleteMapping("/admin/waitings")
    public ResponseEntity<Void> deleteWaiting(@RequestParam(name = "id") long id) {
        waitingService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
