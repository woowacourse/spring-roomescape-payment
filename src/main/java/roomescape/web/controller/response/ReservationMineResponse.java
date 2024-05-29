package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.response.ReservationDto;

public record ReservationMineResponse(
        Long reservationId,
        ThemeResponse theme,
        LocalDate date,
        ReservationTimeResponse time
) {

    public static ReservationMineResponse from(ReservationDto reservationDto) {
        return new ReservationMineResponse(
                reservationDto.id(),
                ThemeResponse.from(reservationDto.themeDto()),
                reservationDto.date().getDate(),
                ReservationTimeResponse.from(reservationDto.reservationTimeDto())
        );
    }
}
