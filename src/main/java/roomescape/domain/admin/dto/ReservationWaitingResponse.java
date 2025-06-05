package roomescape.domain.admin.dto;

import java.time.LocalDate;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.theme.dto.ThemeResponse;
import roomescape.domain.time.dto.ReservationTimeResponse;
import roomescape.domain.waiting.entity.Waiting;

public record ReservationWaitingResponse(
        Long id,
        MemberResponse name,
        ThemeResponse theme,
        LocalDate date,
        ReservationTimeResponse startAt
) {

    public static ReservationWaitingResponse from(final Waiting waiting) {
        return new ReservationWaitingResponse(
                waiting.getId(),
                MemberResponse.from(waiting.getMember()),
                ThemeResponse.from(waiting.getTheme()),
                waiting.getDate(),
                ReservationTimeResponse.from(waiting.getTime())
        );
    }
}
