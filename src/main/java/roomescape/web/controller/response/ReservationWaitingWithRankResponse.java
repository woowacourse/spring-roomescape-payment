package roomescape.web.controller.response;

import roomescape.service.response.ReservationWaitingWithRankDto;

import java.time.LocalDate;

public record ReservationWaitingWithRankResponse(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        Long order,
        String deniedAt) {

    public ReservationWaitingWithRankResponse(ReservationWaitingWithRankDto response) {
        this(
                response.id(),
                response.name(),
                response.date().getDate(),
                new ReservationTimeResponse(response.reservationTimeDto()),
                new ThemeResponse(response.themeDto()),
                response.rank(),
                response.deniedAt()
        );
    }
}
