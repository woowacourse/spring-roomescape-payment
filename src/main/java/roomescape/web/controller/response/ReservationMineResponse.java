package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.response.ReservationDto;

public record ReservationMineResponse(Long reservationId, ThemeResponse theme, LocalDate date,
                                      ReservationTimeResponse time) {

    public ReservationMineResponse(ReservationDto reservation) {
        this(reservation.id(),
                ThemeResponse.from(reservation.themeDto()),
                reservation.date().getDate(),
                ReservationTimeResponse.from(reservation.reservationTimeDto()));
    }
}
