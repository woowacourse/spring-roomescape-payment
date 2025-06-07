package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.service.WaitingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/waitings")
@Tag(name = "어드민 대기", description = "어드민 대기 관련 API")
public class AdminWaitingController {

    private final WaitingService waitingService;

    @Operation(summary = "대기 승인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @PostMapping("/{id}/approve")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> approveWaitingByAdmin(
            @PathVariable("id") Long id
    ) {
        waitingService.approveWaiting(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모든 대기 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @GetMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<List<WaitingResponse>> getAllWaitings() {
        List<WaitingResponse> responses = waitingService.getAllWaitings();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "대기 거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    @DeleteMapping("/{id}/reject")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> rejectWaitingByAdmin(
            @PathVariable("id") Long id
    ) {
        waitingService.deleteWaitingByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
