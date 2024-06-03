package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

public record ReservationAppResponse(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeAppResponse reservationTimeAppResponse,
        ThemeAppResponse themeAppResponse
) {

    public static ReservationAppResponse from(Reservation reservation) {
        Member member = reservation.getMember();
        ReservationTime time = reservation.getTime();
        Theme theme = reservation.getTheme();
        return new ReservationAppResponse(
                reservation.getId(),
                member.getName(),
                reservation.getDate(),
                ReservationTimeAppResponse.from(time),
                ThemeAppResponse.from(theme)
        );
    }
}
