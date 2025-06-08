package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.auth.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.waiting.MemberWaitingCreateRequest;
import roomescape.dto.waiting.WaitingCreateRequest;
import roomescape.dto.waiting.WaitingResponse;
import roomescape.service.WaitingService;

@RestController
@RequestMapping("/waitings")
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(final WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @Operation(summary = "예약 대기 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<WaitingResponse>> getAllWaitings() {
        return ResponseEntity.ok().body(waitingService.findAllWaitings());
    }

    @Operation(summary = "예약 대기 추가 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 대기 추가 성공")
    })
    @PostMapping
    public ResponseEntity<WaitingResponse> addWaiting(@CurrentMember LoginInfo loginInfo,
                                                      @RequestBody final MemberWaitingCreateRequest request) {
        WaitingCreateRequest waitingCreateRequest = new WaitingCreateRequest(request.date(),
                loginInfo.id(),
                request.themeId(),
                request.timeId());
        WaitingResponse response = waitingService.createWaiting(waitingCreateRequest);
        return ResponseEntity.created(URI.create("waitings/" + response.id())).body(response);
    }

    @Operation(summary = "예약 대기 승인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 대기 승인 성공")
    })
    @PostMapping("/{id}")
    public ResponseEntity<Void> approveWaiting(@PathVariable("id") final Long id) {
        waitingService.approveWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 대기 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 대기 삭제 성공")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") final Long id) {
        waitingService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
