package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWaiting;
import roomescape.domain.ReservationWaitingWithRank;
import roomescape.domain.Theme;

public record ReservationWaitingWithRankDto(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeDto reservationTimeDto,
        ThemeDto themeDto,
        Long rank,
        String deniedAt) {

    public static ReservationWaitingWithRankDto from(ReservationWaitingWithRank waitingWithRank) {
        ReservationWaiting waiting = waitingWithRank.getWaiting();
        Member member = waiting.getMember();
        ReservationTime time = waiting.getTime();
        Theme theme = waiting.getTheme();
        return new ReservationWaitingWithRankDto(
                waiting.getId(),
                member.getName(),
                waiting.getDate(),
                ReservationTimeDto.from(time),
                ThemeDto.from(theme),
                waitingWithRank.getRank(),
                waiting.getDeniedAt()
        );
    }
}
