package roomescape.time.presentation;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getReservationTimeList() {
        return ResponseEntity.ok(reservationTimeService.findAllReservationTime());
    }

    @GetMapping("/times/available")
    public ResponseEntity<List<AvailableTimeResponse>> readTimesWithReservationStatus(
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "themeId") Long themeId) {
        return ResponseEntity.ok(reservationTimeService.findAllWithReservationStatus(date, themeId));
    }
}
