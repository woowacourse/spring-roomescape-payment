package roomescape.presentation.dto.response;

import java.time.LocalDate;
import roomescape.business.model.entity.Waiting;

public record WaitingResponse(
        String id,
        UserResponse user,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme
) {
    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId().value(),
                UserResponse.from(waiting.getUser()),
                waiting.getDate().value(),
                ReservationTimeResponse.from(waiting.getTime()),
                ThemeResponse.from(waiting.getTheme())
        );
    }
}
