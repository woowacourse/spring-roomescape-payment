package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "예약 검색 요청 Query Parameter")
public record ReservationSearchRequestParameter(
        @Schema(description = "검색 범위 시작 날짜", example = "2024-06-25") LocalDate dateFrom,
        @Schema(description = "검색 범위 마지막 날짜", example = "2024-06-30") LocalDate dateTo,
        @Schema(description = "사용자 pk", example = "1") Long memberId,
        @Schema(description = "테마 pk", example = "1") Long themeId
) {
}
