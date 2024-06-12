package roomescape.web.controller.response;

import roomescape.service.response.ReservationWaitingDto;

import java.time.LocalDate;

public record ReservationWaitingResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String status) {

    public ReservationWaitingResponse(ReservationWaitingDto response) {
        this(
                response.id(),
                response.name(),
                response.date().getDate(),
                new ReservationTimeResponse(response.reservationTimeDto()),
                new ThemeResponse(response.themeDto()),
                response.status()
        );
    }
}
