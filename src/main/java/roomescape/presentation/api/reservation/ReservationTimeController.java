package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.query.ReservationTimeQueryService;
import roomescape.application.reservation.query.dto.AvailableReservationTimeResult;
import roomescape.application.reservation.query.dto.ReservationTimeResult;
import roomescape.presentation.api.reservation.response.AvailableReservationTimeResponse;
import roomescape.presentation.api.reservation.response.ReservationTimeResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "예약 시간 API")
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeQueryService reservationTimeQueryService;

    public ReservationTimeController(final ReservationTimeQueryService reservationTimeQueryService) {
        this.reservationTimeQueryService = reservationTimeQueryService;
    }

    @Operation(
            summary = "예약 시간 조회",
            description = "모든 예약 시간을 조회합니다. 예약 가능한 시간도 포함됩니다."
    )
    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        final List<ReservationTimeResult> reservationTimeResults = reservationTimeQueryService.findAll();
        final List<ReservationTimeResponse> reservationTimeResponses = reservationTimeResults.stream()
                .map(ReservationTimeResponse::from)
                .toList();
        return ResponseEntity.ok(reservationTimeResponses);
    }

    @Operation(
            summary = "예약 가능한 시간 조회",
            description = "특정 테마와 날짜에 대한 예약 가능한 시간을 조회합니다."
    )
    @GetMapping("/available")
    public ResponseEntity<List<AvailableReservationTimeResponse>> findAll(@RequestParam("themeId") final Long themeId,
                                                                          @RequestParam("date") final LocalDate reservationDate) {
        final List<AvailableReservationTimeResult> availableTimes = reservationTimeQueryService.findAvailableTimesByThemeIdAndDate(
                themeId,
                reservationDate
        );
        final List<AvailableReservationTimeResponse> availableReservationTimeResponses = availableTimes.stream()
                .map(AvailableReservationTimeResponse::from)
                .toList();
        return ResponseEntity.ok(availableReservationTimeResponses);
    }
}
