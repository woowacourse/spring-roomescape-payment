package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationFindFilteredRequest(

        @Schema(description = "테마 Id", example = "1")
        @NotNull Long themeId,

        @Schema(description = "멤버 Id", example = "1")
        @NotNull Long memberId,

        @Schema(description = "조회 시작일 (해당 날짜 미포함)", example = "2025-01-01")
        @NotNull LocalDate dateFrom,

        @Schema(description = "조회 종료일 (해당 날짜 포함)", example = "2026-01-01")
        @NotNull LocalDate dateTo
) {
}
