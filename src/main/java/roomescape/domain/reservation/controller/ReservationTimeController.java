package roomescape.domain.reservation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.reservation.dto.AvailableReservationTimeResponse;
import roomescape.domain.reservation.service.ReservationTimeService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(final ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/available-reservation-times")
    public List<AvailableReservationTimeResponse> getAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("theme-id") final Long themeId
    ) {
        return reservationTimeService.getAvailableReservationTimes(date, themeId)
                .values()
                .entrySet()
                .stream()
                .map(entry -> AvailableReservationTimeResponse.of(entry.getKey(), entry.getValue()))
                .toList();
    }
}
