package roomescape.reservationTime.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.reservationTime.application.ReservationTimeService;
import roomescape.reservationTime.application.dto.TimeRequest;
import roomescape.reservationTime.application.dto.TimeResponse;

@RestController
@AllArgsConstructor
@RequestMapping("admin/times")
@Slf4j
public class AdminReservationTimeController {
    private final ReservationTimeService timeService;

    @Operation(
            summary = "예약 시간 생성",
            description = "새로운 예약 시간을 등록합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<TimeResponse>> create(@Valid @RequestBody TimeRequest request) {
        log.info("예약 시간 생성 요청: time={}", request.startAt());
        TimeResponse response = timeService.create(request);
        ApiResponse<TimeResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @Operation(
            summary = "전체 예약 시간 조회",
            description = "등록된 모든 예약 시간을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeResponse>>> getAll() {
        List<TimeResponse> response = timeService.findAll();
        ApiResponse<List<TimeResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "예약 시간 삭제",
            description = "예약 시간을 ID로 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        log.info("예약 시간 삭제 요청 id={}", id);
        timeService.deleteById(id);
        ApiResponse<Void> apiResponse = ApiResponse.createSuccessWithNoData();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }
}
