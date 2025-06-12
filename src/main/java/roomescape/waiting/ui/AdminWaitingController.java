package roomescape.waiting.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.waiting.application.WaitingService;
import roomescape.waiting.application.dto.WaitingResponse;

@Tag(name = "예약 대기 API For 관리자", description = "관리자만 호출 가능한 예약 대기 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("admin/waitings")
public class AdminWaitingController {
    private final WaitingService waitingService;

    @Operation(summary = "예약 대기 조회", description = "모든 예약 대기를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<WaitingResponse>>> getAll() {
        List<WaitingResponse> response = waitingService.findAll();
        ApiResponse<List<WaitingResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "예약 대기 삭제", description = "예약 대기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        waitingService.deleteByAdmin(id);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
