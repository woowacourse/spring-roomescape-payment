package roomescape.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "예약 검색 요청 DTO")
public record ReservationSearchRequest(
        @Schema(description = "테마 ID", example = "1")
        Long themeId,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "예약 시작 날짜", example = "2026-06-07")
        LocalDate dateFrom,

        @Schema(description = "예약 종료 날짜", example = "2026-06-31")
        LocalDate dateTo
) {
}
