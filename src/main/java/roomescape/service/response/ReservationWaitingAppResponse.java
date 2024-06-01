package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWaiting;
import roomescape.domain.Theme;

public record ReservationWaitingAppResponse(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeAppResponse reservationTimeAppResponse,
        ThemeAppResponse themeAppResponse,
        String status
) {

    public static ReservationWaitingAppResponse from(ReservationWaiting waiting) {
        Member member = waiting.getMember();
        ReservationDate date = waiting.getDate();
        ReservationTime time = waiting.getTime();
        Theme theme = waiting.getTheme();
        return new ReservationWaitingAppResponse(
                waiting.getId(),
                member.getName(),
                date,
                ReservationTimeAppResponse.from(time),
                ThemeAppResponse.from(theme),
                waiting.getDeniedAt());
    }
}
