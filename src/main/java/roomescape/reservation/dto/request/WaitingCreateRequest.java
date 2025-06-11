package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WaitingCreateRequest(

        @Schema(description = "대기 날짜", example = "2025-05-21")
        @NotNull LocalDate date,

        @Schema(description = "시간 Id", example = "1")
        @NotNull Long timeId,

        @Schema(description = "테마 Id", example = "1")
        @NotNull Long themeId
) {
}
