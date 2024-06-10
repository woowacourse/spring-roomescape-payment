package roomescape.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.WaitingService;

@Tag(name = "예약대기 API(어드민전용)", description = "어드민 전용API로 어드민의 토큰이 필요합니다.")
@RestController
@RequestMapping("/admin/waitings")
public class AdminWaitingController {

    private final WaitingService waitingService;

    public AdminWaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약대기 목록", description = "예약대기 목록을 불러옵니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findWaiting() {
        final List<ReservationResponse> waitings = waitingService.findAllWaitings();
        return ResponseEntity.ok(waitings);
    }

    @Operation(summary = "예약대기 승인", description = "예약대기를 승인하여 예약으로 승격합니다.")
    @PutMapping("/{waitingId}")
    public ResponseEntity<ReservationResponse> approveWaiting(@PathVariable final Long waitingId) {
        final ReservationResponse response = waitingService.approve(waitingId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약대기 취소", description = "예약대기를 취소시킵니다.")
    @DeleteMapping("/{waitingId}")
    public ResponseEntity<Void> denyWaiting(@PathVariable final Long waitingId) {
        waitingService.deny(waitingId);

        return ResponseEntity.noContent().build();
    }
}
