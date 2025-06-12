package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationSearchDto(
        @Schema(description = "검색하고자 하는 테마의 ID")
        @NotNull Long themeId,

        @Schema(description = "검색하고자 하는 회원의 ID")
        @NotNull Long memberId,

        @Schema(description = "검색하고자 날짜의 범위 중 시작 날짜", example = "2022-03-14")
        @NotNull LocalDate startDate,

        @Schema(description = "검색하고자 날짜의 범위 중 종료 날짜", example = "2022-03-14")
        @NotNull LocalDate endDate
) {
}
