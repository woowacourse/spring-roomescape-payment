package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Member Waiting Request Model")
public record MemberWaitingRequest(@Schema(description = "Waiting date", example = "2023-12-25")
                                   LocalDate date,

                                   @Schema(description = "Time Slot ID", example = "1")
                                   Long timeId,

                                   @Schema(description = "Theme ID", example = "1")
                                   Long themeId) {

    public MemberWaitingRequest {
        isValid(date, timeId, themeId);
    }

    private void isValid(LocalDate date, Long timeId, Long themeId) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 날짜입니다.");
        }

        if (timeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 시간입니다.");
        }

        if (themeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 테마 입니다.");
        }
    }
}
