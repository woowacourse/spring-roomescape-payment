package roomescape.application.dto.request.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(name = "예약 검색 조건")
public record ReservationSearchCondition(
        @Schema(description = "시작일", example = "2024-10-10")
        @NotNull(message = "시작일은 비어있을 수 없습니다.")
        LocalDate start,

        @Schema(description = "종료일", example = "2024-10-10")
        @NotNull(message = "종료일은 비어있을 수 없습니다.")
        LocalDate end,

        @Schema(description = "예약자 ID", example = "1")
        @Positive(message = "예약자명은 비어있을 수 없습니다.")
        Long memberId,

        @Schema(description = "테마 ID", example = "1")
        @Positive(message = "테마명은 비어있을 수 없습니다.")
        Long themeId
) {
}
