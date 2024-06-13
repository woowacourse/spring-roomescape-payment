package roomescape.waiting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoggedInMember;
import roomescape.waiting.dto.WaitingCreateRequest;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.service.WaitingService;

@Tag(name = "대기 API")
@RestController
@RequestMapping("/waitings")
public class WaitingController {
    private final WaitingService service;

    public WaitingController(WaitingService service) {
        this.service = service;
    }

    @Operation(summary = "전체 예약 대기 조회", description = "전체 예약 대기를 조회한다.")
    @GetMapping
    public List<WaitingResponse> findWaitings() {
        return service.findWaitings();
    }

    @Operation(summary = "예약 대기 추가", description = "예약 대기를 추가한다.")
    @PostMapping
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody WaitingCreateRequest request,
            LoggedInMember member) {
        WaitingResponse response = service.createWaiting(request, member.id());

        URI location = URI.create("/waitings/" + response.id());
        return ResponseEntity.created(location)
                .body(response);
    }

    @Operation(summary = "예약 대기 삭제", description = "예약 대기를 삭제한다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(@PathVariable Long id) {
        service.deleteWait(id);
    }
}
