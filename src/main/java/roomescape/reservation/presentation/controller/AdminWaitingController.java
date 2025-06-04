package roomescape.reservation.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.Auth;
import roomescape.member.domain.Role;
import roomescape.reservation.application.service.WaitingService;
import roomescape.reservation.presentation.dto.WaitingResponse;

@RestController
@RequestMapping("/admin/reservations/waiting")
public class AdminWaitingController {

    private final WaitingService waitingService;

    public AdminWaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Auth(Role.ADMIN)
    @Operation(summary = "관리자 예약 대기 조회 API")
    @GetMapping
    public ResponseEntity<List<WaitingResponse>> getWaitings(
    ) {
        return ResponseEntity.ok().body(
                waitingService.getWaitings()
        );
    }

    @Auth(Role.ADMIN)
    @Operation(summary = "관리자 예약 대기 승인 API")
    @DeleteMapping("/accept/{id}")
    public ResponseEntity<Void> acceptWaiting(
            final @PathVariable Long id
    ) {
        waitingService.acceptWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @Auth(Role.ADMIN)
    @Operation(summary = "관리자 예약 대기 거절 API")
    @DeleteMapping("/reject/{id}")
    public ResponseEntity<Void> rejectWaiting(
            final @PathVariable Long id
    ) {
        waitingService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
