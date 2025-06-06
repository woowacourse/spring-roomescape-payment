package roomescape.reservation.dto.response;

import java.time.LocalDate;

import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Waiting;
import roomescape.theme.dto.response.ThemeResponse;

public record WaitingResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme
) {

    public static WaitingResponse from(final Waiting waiting) {
        ReservationTimeResponse reservationTimeResponse = ReservationTimeResponse.from(
                waiting.getTime()
        );
        ThemeResponse themeResponse = ThemeResponse.from(waiting.getTheme());
        MemberResponse memberResponse = MemberResponse.fromEntity(waiting.getMember());

        return new WaitingResponse(
                waiting.idValue(),
                memberResponse,
                waiting.getDate(),
                reservationTimeResponse,
                themeResponse
        );
    }
}
