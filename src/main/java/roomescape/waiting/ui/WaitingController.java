package roomescape.waiting.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.common.response.ApiResponse;
import roomescape.waiting.application.WaitingService;
import roomescape.waiting.application.dto.WaitingRequest;
import roomescape.waiting.application.dto.WaitingResponse;

@Tag(name = "예약 대기 API", description = "예약 대기 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("waitings")
public class WaitingController {
    private final WaitingService waitingService;

    @Operation(summary = "예약 대기 생성", description = "예약 대기를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<WaitingResponse>> add(
            @Parameter(hidden = true) @LoginMemberId Long memberId,
            @Valid @RequestBody WaitingRequest request
    ) {
        WaitingResponse response = waitingService.create(memberId, request);
        ApiResponse<WaitingResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(summary = "예약 대기 삭제", description = "멤버 본인의 예약 대기를 삭제합니다")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") Long id,
            @Parameter(hidden = true) @LoginMemberId Long memberId) {
        waitingService.deleteByUser(id, memberId);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
