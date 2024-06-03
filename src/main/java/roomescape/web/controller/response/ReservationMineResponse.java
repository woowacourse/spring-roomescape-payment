package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.response.ReservationAppResponse;

public record ReservationMineResponse(
        Long reservationId,
        ThemeResponse theme,
        LocalDate date,
        ReservationTimeResponse time
) {

    public static ReservationMineResponse from(ReservationAppResponse reservationAppResponse) {
        return new ReservationMineResponse(
                reservationAppResponse.id(),
                ThemeResponse.from(reservationAppResponse.themeAppResponse()),
                reservationAppResponse.date().getDate(),
                ReservationTimeResponse.from(reservationAppResponse.reservationTimeAppResponse())
        );
    }
}
