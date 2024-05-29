package roomescape.web.controller.response;

import java.time.LocalDate;
import roomescape.service.response.ReservationWaitingWithRankDto;

public record ReservationWaitingWithRankResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        Long order,
        String deniedAt
) {

    public static ReservationWaitingWithRankResponse from(ReservationWaitingWithRankDto response) {
        return new ReservationWaitingWithRankResponse(
                response.id(),
                response.name(),
                response.date().getDate(),
                ReservationTimeResponse.from(response.reservationTimeDto()),
                ThemeResponse.from(response.themeDto()),
                response.rank(),
                response.deniedAt()
        );
    }
}
