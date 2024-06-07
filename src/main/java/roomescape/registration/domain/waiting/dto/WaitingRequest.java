package roomescape.registration.domain.waiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "대기 요청")
public record WaitingRequest(

        @Schema(description = "대기 날짜", example = "2024-06-05")
        LocalDate date,

        @Schema(description = "시간 ID", example = "2")
        long timeId,

        @Schema(description = "테마 ID", example = "1")
        long themeId) {
}
