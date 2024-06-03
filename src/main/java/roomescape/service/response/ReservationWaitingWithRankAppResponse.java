package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWaiting;
import roomescape.domain.ReservationWaitingWithRank;
import roomescape.domain.Theme;

public record ReservationWaitingWithRankAppResponse(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeAppResponse reservationTimeAppResponse,
        ThemeAppResponse themeAppResponse,
        Long rank,
        String deniedAt) {

    public static ReservationWaitingWithRankAppResponse from(ReservationWaitingWithRank waitingWithRank) {
        ReservationWaiting waiting = waitingWithRank.getWaiting();
        Member member = waiting.getMember();
        ReservationTime time = waiting.getTime();
        Theme theme = waiting.getTheme();
        return new ReservationWaitingWithRankAppResponse(
                waiting.getId(),
                member.getName(),
                waiting.getDate(),
                ReservationTimeAppResponse.from(time),
                ThemeAppResponse.from(theme),
                waitingWithRank.getRank(),
                waiting.getDeniedAt()
        );
    }
}
