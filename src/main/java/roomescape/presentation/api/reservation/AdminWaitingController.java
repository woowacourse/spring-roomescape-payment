package roomescape.presentation.api.reservation;

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
@RequestMapping("/admin/waitings")
public class AdminWaitingController {

    private final WaitingQueryService waitingQueryService;
    private final DeleteWaitingService deleteWaitingService;

    public AdminWaitingController(final WaitingQueryService waitingQueryService,
                                  final DeleteWaitingService deleteWaitingService) {
        this.waitingQueryService = waitingQueryService;
        this.deleteWaitingService = deleteWaitingService;
    }

    @GetMapping
    public ResponseEntity<List<WaitingResponse>> findAll() {
        final List<WaitingResponse> waitingResponses = waitingQueryService.findAll()
                .stream()
                .map(WaitingResponse::from)
                .toList();
        return ResponseEntity.ok(waitingResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@AuthPrincipal final AuthInfo authInfo, @PathVariable("id") final Long waitingId) {
        deleteWaitingService.cancel(waitingId, authInfo.memberId());
        return ResponseEntity.noContent().build();
    }
}
