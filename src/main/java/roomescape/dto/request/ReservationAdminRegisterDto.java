package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationAdminRegisterDto(
        @Schema(description = "예약을 원하는 날짜", example = "2022-03-14")
        @NotNull LocalDate date,

        @Schema(description = "예약을 원하는 테마의 ID")
        @NotNull Long themeId,

        @Schema(description = "예약을 원하는 예약 시각의 ID")
        @NotNull Long timeId,

        @Schema(description = "예약을 원하는 회원의 ID")
        @NotNull Long memberId
) {
}
