package roomescape.presentation.dto.response;

import java.time.LocalDate;
import roomescape.business.model.entity.Waiting;

public record WaitingResponse(
        String id,
        MemberResponse user,
        LocalDate date,
        TimeSlotResponse time,
        ThemeResponse theme
) {
    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId().value(),
                MemberResponse.from(waiting.getMember()),
                waiting.getDate().value(),
                TimeSlotResponse.from(waiting.getTime()),
                ThemeResponse.from(waiting.getTheme())
        );
    }
}
