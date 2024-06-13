package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ReservationRequest(
        @Schema(description = "예약 날짜")
        LocalDate date,

        @Schema(description = "회원 ID")
        Long memberId,

        @Schema(description = "시간 ID")
        long timeId,

        @Schema(description = "테마 ID")
        long themeId
) {
}
