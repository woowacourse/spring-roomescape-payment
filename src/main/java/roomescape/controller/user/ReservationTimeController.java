package roomescape.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;
import roomescape.service.reservation.ReservationTimeService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "3. 예약 시간 관련 API")
@RequiredArgsConstructor
@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "모든 예약 시간 조회")
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getAll() {
        List<ReservationTimeResponse> response = reservationTimeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "테마, 날짜의 예약 가능 시간 조회")
    @GetMapping("/times/{themeId}/available")
    public ResponseEntity<List<ReservationTimeWithAvailabilityResponse>> getAvailables(
            @PathVariable long themeId,
            @RequestParam LocalDate date
    ) {
        List<ReservationTimeWithAvailabilityResponse> response = reservationTimeService.getAllWithAvailabilityBy(themeId, date);
        return ResponseEntity.ok(response);
    }
}
