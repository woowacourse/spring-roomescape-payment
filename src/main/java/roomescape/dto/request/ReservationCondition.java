package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Optional;

@Schema(description = "예약 조건 검색을 위한 객체")
public record ReservationCondition(
        @Schema(description = "테마 ID (선택)", example = "1")
        Optional<Long> themeId,

        @Schema(description = "회원 ID (선택)", example = "4")
        Optional<Long> memberId,

        @Schema(description = "조회 시작 날짜 (선택)", example = "2023-12-01")
        Optional<LocalDate> dateFrom,

        @Schema(description = "조회 종료 날짜 (선택)", example = "2023-12-31")
        Optional<LocalDate> dateTo
) {
}
