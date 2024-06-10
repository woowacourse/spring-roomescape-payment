package roomescape.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.controller.dto.response.TimeAndAvailabilityResponse;
import roomescape.service.ReservationTimeService;

@Tag(name = "UserReservationTime", description = "사용자도 확인 가능한 방탈출 예약 가능 시간 관련 API")
@RestController
@RequestMapping("/times")
public class UserReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public UserReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @Operation(summary = "예약 가능한 시간 조회", description = "예약 가능 여부와 예약 시간을 함께 조회할 수 있다.")
    @GetMapping("/available")
    public ResponseEntity<List<TimeAndAvailabilityResponse>> findAllWithAvailability(
            @RequestParam LocalDate date, @RequestParam Long id) {

        List<TimeAndAvailabilityResponse> response = reservationTimeService.findAllWithBookAvailability(date, id);
        return ResponseEntity.ok(response);
    }
}
