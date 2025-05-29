package roomescape.presentation.api.reservation;

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
@RequestMapping("/times")
public class ReservationTimeController {

    private final ReservationTimeQueryService reservationTimeQueryService;

    public ReservationTimeController(final ReservationTimeQueryService reservationTimeQueryService) {
        this.reservationTimeQueryService = reservationTimeQueryService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAll() {
        final List<ReservationTimeResult> reservationTimeResults = reservationTimeQueryService.findAll();
        final List<ReservationTimeResponse> reservationTimeResponses = reservationTimeResults.stream()
                .map(ReservationTimeResponse::from)
                .toList();
        return ResponseEntity.ok(reservationTimeResponses);
    }

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
