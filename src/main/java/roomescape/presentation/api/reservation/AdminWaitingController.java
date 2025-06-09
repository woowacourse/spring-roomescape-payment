package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.command.DeleteWaitingService;
import roomescape.application.reservation.query.WaitingQueryService;
import roomescape.presentation.api.reservation.response.WaitingResponse;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthPrincipal;

import java.util.List;

@RestController
@Tag(name = "관리자 대기열 API")
@RequestMapping("/admin/waitings")
public class AdminWaitingController {

    private final WaitingQueryService waitingQueryService;
    private final DeleteWaitingService deleteWaitingService;

    public AdminWaitingController(final WaitingQueryService waitingQueryService,
                                  final DeleteWaitingService deleteWaitingService) {
        this.waitingQueryService = waitingQueryService;
        this.deleteWaitingService = deleteWaitingService;
    }

    @Operation(
            summary = "관리자 대기열 조회",
            description = "관리자가 모든 대기열을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<List<WaitingResponse>> findAll() {
        final List<WaitingResponse> waitingResponses = waitingQueryService.findAll()
                .stream()
                .map(WaitingResponse::from)
                .toList();
        return ResponseEntity.ok(waitingResponses);
    }

    @Operation(
            summary = "관리자 대기열 삭제",
            description = "관리자가 대기열을 삭제합니다. 대기열 ID를 경로 변수로 전달해야 합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@AuthPrincipal final AuthInfo authInfo, @PathVariable("id") final Long waitingId) {
        deleteWaitingService.cancel(waitingId, authInfo.memberId());
        return ResponseEntity.noContent().build();
    }
}
