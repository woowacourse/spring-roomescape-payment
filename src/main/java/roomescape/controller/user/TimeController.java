package roomescape.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.api.MemberTimeApi;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;
import roomescape.service.reservation.ReservationTimeService;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TimeController implements MemberTimeApi {

    private final ReservationTimeService reservationTimeService;

    @Override
    @GetMapping("/times/{themeId}/available")
    public ResponseEntity<List<ReservationTimeWithAvailabilityResponse>> getAvailables(
            @PathVariable long themeId,
            @RequestParam LocalDate date
    ) {
        List<ReservationTimeWithAvailabilityResponse> response = reservationTimeService.getAllWithAvailabilityBy(themeId, date);
        return ResponseEntity.ok(response);
    }
}
