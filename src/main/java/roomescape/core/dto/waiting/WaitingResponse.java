package roomescape.core.dto.waiting;

import java.time.format.DateTimeFormatter;
import roomescape.core.domain.Waiting;
import roomescape.core.dto.member.MemberResponse;
import roomescape.core.dto.reservationtime.ReservationTimeResponse;
import roomescape.core.dto.theme.ThemeResponse;

public record WaitingResponse(Long id, MemberResponse member, String date, ReservationTimeResponse time,
                              ThemeResponse theme) {

    public static WaitingResponse from(final Waiting waiting) {
        final Long id = waiting.getId();
        final MemberResponse member = MemberResponse.from(waiting.getMember());
        final String date = waiting.getDate().format(DateTimeFormatter.ISO_DATE);
        final ReservationTimeResponse time = ReservationTimeResponse.from(waiting.getTime());
        final ThemeResponse theme = ThemeResponse.from(waiting.getTheme());

        return new WaitingResponse(id, member, date, time, theme);
    }
}
