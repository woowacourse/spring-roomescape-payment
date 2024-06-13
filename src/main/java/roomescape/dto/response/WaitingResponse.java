package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Waiting;

import java.time.LocalDate;

@Schema(description = "Waiting Response Model")
public record WaitingResponse(@Schema(description = "Waiting ID", example = "123")
                              Long id,

                              @Schema(description = "Member information")
                              MemberResponse member,

                              @Schema(description = "Reservation date", example = "2024-06-12")
                              LocalDate date,

                              @Schema(description = "Time slot information")
                              TimeSlotResponse time,

                              @Schema(description = "Theme information")
                              ThemeResponse theme) {

    public static WaitingResponse from(Waiting waiting) {
        return new WaitingResponse(
                waiting.getId(),
                MemberResponse.from(waiting.getMember()),
                waiting.getDate(),
                TimeSlotResponse.from(waiting.getTime()),
                ThemeResponse.from(waiting.getTheme())
        );
    }
}
