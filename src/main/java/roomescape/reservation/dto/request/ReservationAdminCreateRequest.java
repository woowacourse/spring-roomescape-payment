package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationAdminCreateRequest(

        @Schema(description = "예약 날짜", example = "2026-01-01")
        @NotNull LocalDate date,

        @Schema(description = "테마 Id", example = "1")
        @NotNull Long themeId,

        @Schema(description = "시간 Id", example = "1")
        @NotNull Long timeId,

        @Schema(description = "멤버 Id", example = "1")
        @NotNull Long memberId
) {
}
