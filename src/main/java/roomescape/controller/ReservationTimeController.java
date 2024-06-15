package roomescape.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationTimeService;
import roomescape.dto.response.reservation.AvailableTimeResponse;
import roomescape.dto.response.reservation.ReservationTimeResponse;

@RestController
@RequestMapping("/times")
public class ReservationTimeController {
    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationTimeResponse>> findAllTimes() {
        List<ReservationTimeResponse> responses = reservationTimeService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableTimeResponse>> findAvailableTimes(
            @RequestParam LocalDate date,
            @RequestParam long themeId
    ) {
        List<AvailableTimeResponse> responses = reservationTimeService.findAvailableTimes(date, themeId);
        return ResponseEntity.ok(responses);
    }
}
