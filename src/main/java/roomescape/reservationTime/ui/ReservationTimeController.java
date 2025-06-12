package roomescape.reservationTime.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.reservationTime.application.ReservationTimeService;
import roomescape.reservationTime.application.dto.AvailableTimeRequest;
import roomescape.reservationTime.application.dto.AvailableTimeResponse;

@Tag(name = "예약 시간 API", description = "예약 시간 관련 API입니다.")
@RestController
@AllArgsConstructor
@RequestMapping("times")
public class ReservationTimeController {
    private final ReservationTimeService timeService;

    @Operation(summary = "예약 가능 시간 조회", description = "날짜, 테마에 대해 예약 가능한 시간을 구분하여 조회합니다.")
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<AvailableTimeResponse>>> get(
            @Valid @ModelAttribute AvailableTimeRequest request
    ) {
        List<AvailableTimeResponse> response = timeService.findAvailableTimes(request);
        ApiResponse<List<AvailableTimeResponse>> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }
}

