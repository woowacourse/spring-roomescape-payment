package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.response.ReservationWaitingWithRankAppResponse;

public record ReservationWaitingWithRankResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        Long order,
        String deniedAt
) {

    public static ReservationWaitingWithRankResponse from(ReservationWaitingWithRankAppResponse response) {
        return new ReservationWaitingWithRankResponse(
                response.id(),
                response.name(),
                response.date().getDate(),
                ReservationTimeResponse.from(response.reservationTimeAppResponse()),
                ThemeResponse.from(response.themeAppResponse()),
                response.rank(),
                response.deniedAt()
        );
    }
}
