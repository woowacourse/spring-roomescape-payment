package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationCreateRequest(
        @NotNull(message = "예약 일자는 필수입니다.")
        LocalDate date,

        @NotNull(message = "예약 시간은 필수입니다.")
        @Schema(example = "1")
        Long timeId,

        @NotNull(message = "예약 테마는 필수입니다.")
        @Schema(example = "1")
        Long themeId
) {
}
