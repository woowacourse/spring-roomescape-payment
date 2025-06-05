package roomescape.domain.waiting.dto;

import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;
import roomescape.domain.waiting.entity.Waiting;

public record CreateWaitingResponse(
        Long id,
        LocalDate date,
        MemberResponse member,
        ReservationTimeResponse time,
        ThemeResponse theme
) {
    public static CreateWaitingResponse from(final Waiting waiting) {
        return new CreateWaitingResponse(
                waiting.getId(),
                waiting.getDate(),
                MemberResponse.from(waiting.getMember()),
                ReservationTimeResponse.from(waiting.getTime()),
                ThemeResponse.from(waiting.getTheme())
        );
    }

}
