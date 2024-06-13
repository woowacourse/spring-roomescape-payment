package roomescape.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.api.docs.UserReservationTimeApiDocs;
import roomescape.controller.dto.response.TimeAndAvailabilityResponse;
import roomescape.service.ReservationTimeService;

@RestController
@RequestMapping("/times")
public class UserReservationTimeController implements UserReservationTimeApiDocs {
    private final ReservationTimeService reservationTimeService;

    public UserReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<TimeAndAvailabilityResponse>> findAllWithAvailability(
            @RequestParam LocalDate date, @RequestParam Long id) {

        List<TimeAndAvailabilityResponse> response = reservationTimeService.findAllWithBookAvailability(date, id);
        return ResponseEntity.ok(response);
    }
}
