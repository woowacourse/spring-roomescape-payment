package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.domain.ReservationDate;
import roomescape.service.response.ReservationWaitingDto;

public record ReservationWaitingResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String status
) {

    public static ReservationWaitingResponse from(ReservationWaitingDto response) {
        ReservationDate date = response.date();
        return new ReservationWaitingResponse(
                response.id(),
                response.name(),
                date.getDate(),
                ReservationTimeResponse.from(response.reservationTimeDto()),
                ThemeResponse.from(response.themeDto()),
                response.status()
        );
    }
}
