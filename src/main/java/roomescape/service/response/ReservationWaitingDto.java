package roomescape.service.response;

import roomescape.domain.Member;
import roomescape.domain.ReservationDate;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationWaiting;
import roomescape.domain.Theme;

public record ReservationWaitingDto(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeDto reservationTimeDto,
        ThemeDto themeDto,
        String status
) {

    public static ReservationWaitingDto from(ReservationWaiting waiting) {
        Member member = waiting.getMember();
        ReservationDate date = waiting.getDate();
        ReservationTime time = waiting.getTime();
        Theme theme = waiting.getTheme();
        return new ReservationWaitingDto(
                waiting.getId(),
                member.getName(),
                date,
                ReservationTimeDto.from(time),
                ThemeDto.from(theme),
                waiting.getDeniedAt());
    }
}
