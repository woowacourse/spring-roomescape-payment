package roomescape.domain.time.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "예약 가능한 시간 요청 DTO")
public record AvailableReservationTimeRequest(
        @Schema(description = "예약 날짜", example = "2026-06-07")
        LocalDate date,

        @Schema(description = "테마 ID", example = "1")
        Long themeId
) {
}
