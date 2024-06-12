package roomescape.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ReservationSearchCondition(
        @Schema(description = "회원 ID")
        long memberId,

        @Schema(description = "테마 ID")
        long themeId,

        @Schema(description = "예약 시작 날짜")
        LocalDate start,

        @Schema(description = "예약 종료 날짜")
        LocalDate end
) {
}
