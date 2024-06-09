package roomescape.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationWaitingService;
import roomescape.service.response.ReservationWaitingAppResponse;
import roomescape.web.controller.response.ReservationWaitingResponse;

@Tag(name = "Admin-ReservationWaiting", description = "운영자 예약대기 API")
@RestController
@RequestMapping("/admin/reservation-waitings")
public class AdminReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public AdminReservationWaitingController(final ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @Operation(summary = "예약 대기 조회", description = "예약 대기 가능한 예약을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationWaitingResponse>> getAvailableWaitings() {
        List<ReservationWaitingResponse> waitingWebResponses = reservationWaitingService.findAllAllowed().stream()
                .map(ReservationWaitingResponse::from)
                .toList();

        return ResponseEntity.ok().body(waitingWebResponses);
    }

    @Operation(summary = "예약 대기 취소", description = "예약 대기 id로 예약 대기를 취소합니다.")
    @PatchMapping("/{id}/deny")
    public ResponseEntity<ReservationWaitingResponse> updateWaitingStatus(@PathVariable Long id) {
        ReservationWaitingAppResponse waitingAppResponse = reservationWaitingService.denyWaiting(id);
        ReservationWaitingResponse waitingWebResponse = ReservationWaitingResponse.from(waitingAppResponse);

        return ResponseEntity.ok(waitingWebResponse);
    }
}
