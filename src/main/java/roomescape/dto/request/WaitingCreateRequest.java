package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "예약대기 생성 요청 객체")
public record WaitingCreateRequest(

        @NotNull
        @Schema(description = "날짜", example = "2026-08-05")
        LocalDate date,

        @NotNull
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,

        @NotNull
        @Schema(description = "테마 ID", example = "2")
        Long themeId
) {
}
