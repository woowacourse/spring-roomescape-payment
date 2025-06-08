package roomescape.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.Authenticated;
import roomescape.dto.request.WaitingCreateRequest;
import roomescape.dto.response.WaitingResponse;
import roomescape.service.WaitingService;

@Tag(name = "예약 대기 API", description = "예약 대기 API 입니다.")
@RestController
@RequestMapping(value = "/api/waiting")
public class WaitingController {
    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약 대기 생성", description = "새 예약 대기를 생성합니다.")
    @PostMapping
    public ResponseEntity<WaitingResponse> createNewWaiting(
            @Authenticated Long memberId,
            @Valid @RequestBody WaitingCreateRequest request) {
        WaitingResponse waitingResponse = waitingService.createWaiting(
                memberId, request.timeId(), request.themeId(), request.date());
        return ResponseEntity
                .created(URI.create("/reservations/waitings/" + waitingResponse.id()))
                .body(waitingResponse);
    }

    @Operation(summary = "예약 대기 삭제", description = "특정 예약 대기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable Long id) {
        waitingService.deleteWaitingById(id);
        return ResponseEntity.noContent().build();
    }
}
