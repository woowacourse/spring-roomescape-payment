package roomescape.service.response;

import roomescape.domain.reservation.ReservationDate;
import roomescape.domain.reservation.ReservationWaitingWithRank;

public record ReservationWaitingWithRankDto(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeDto reservationTimeDto,
        ThemeDto themeDto,
        Long rank,
        String deniedAt) {

    public ReservationWaitingWithRankDto(ReservationWaitingWithRank waitingWithRank) {
        this(
                waitingWithRank.getWaiting().getId(),
                waitingWithRank.getWaiting().getMember().getName().getName(),
                waitingWithRank.getWaiting().getDate(),
                new ReservationTimeDto(waitingWithRank.getWaiting().getTime()),
                new ThemeDto(waitingWithRank.getWaiting().getTheme()),
                waitingWithRank.getRank(),
                waitingWithRank.getWaiting().getDeniedAt()
        );
    }
}
