package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AdminReservationRequest(
        @NotNull
        @Schema(description = "예약 날짜", example = "2024-06-11")
        LocalDate date,
        @NotNull
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        @Schema(description = "예약 테마 ID", example = "1")
        Long themeId,
        @NotNull
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,
        @NotNull
        @Positive(message = "[ERROR] memberId의 값이 1보다 작을 수 없습니다.")
        @Schema(description = "예약 멤버 ID", example = "1")
        Long memberId) {
}
