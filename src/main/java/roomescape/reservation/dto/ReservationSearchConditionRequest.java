package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.validator.ValidDateRange;

@ValidDateRange
@NotNull
public record ReservationSearchConditionRequest(

        @Schema(description = "테마 id", example = "1")
        Long themeId,

        @Schema(description = "회원 id", example = "1")
        Long memberId,

        @Schema(description = "시작 날짜", example = "2024-06-07")
        LocalDate dateFrom,

        @Schema(description = "끝 날짜", example = "2024-06-07")
        LocalDate dateTo) {
}
