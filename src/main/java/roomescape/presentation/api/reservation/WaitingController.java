package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.command.CreateWaitingService;
import roomescape.application.reservation.command.DeleteWaitingService;
import roomescape.presentation.api.reservation.request.CreateWaitingRequest;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthPrincipal;

import java.net.URI;

@Tag(name = "사용자 대기열 API")
@RestController
public class WaitingController {

    private final CreateWaitingService createWaitingService;
    private final DeleteWaitingService deleteWaitingService;

    public WaitingController(final CreateWaitingService createWaitingService, final DeleteWaitingService deleteWaitingService) {
        this.createWaitingService = createWaitingService;
        this.deleteWaitingService = deleteWaitingService;
    }

    @Operation(
            summary = "대기열 생성",
            description = "사용자가 대기열을 생성합니다. 요청 본문에 필요한 대기열 정보를 포함해야 합니다."
    )
    @PostMapping("/reservations/wait")
    public ResponseEntity<Void> createWaiting(@AuthPrincipal final AuthInfo authInfo,
                                              @Valid @RequestBody final CreateWaitingRequest createWaitingRequest) {
        final Long id = createWaitingService.request(createWaitingRequest.toCreateCommand(authInfo.memberId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/reservations/wait/" + id))
                .build();
    }

    @Operation(
            summary = "대기열 취소",
            description = "사용자가 대기열을 취소합니다. 대기열 ID를 경로 변수로 전달해야 합니다."
    )
    @DeleteMapping("/reservations/wait/{waitingId}")
    public ResponseEntity<Void> cancelWaiting(@AuthPrincipal final AuthInfo authInfo,
                                              @PathVariable("waitingId") final Long waitingId) {
        deleteWaitingService.cancel(waitingId, authInfo.memberId());
        return ResponseEntity.noContent().build();
    }
}
