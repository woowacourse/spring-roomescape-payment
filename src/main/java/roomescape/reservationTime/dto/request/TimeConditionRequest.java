package roomescape.reservationTime.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "시간 조건 요청")
public record TimeConditionRequest(
        @Schema(description = "날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "테마 ID", example = "1")
        Long themeId) {
}
