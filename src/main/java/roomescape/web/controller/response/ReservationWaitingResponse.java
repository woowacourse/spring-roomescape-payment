package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.response.ReservationWaitingDto;

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
                ReservationTimeResponse.from(response.reservationTimeDto()),
                ThemeResponse.from(response.themeDto()),
                response.status()
        );
    }
}
