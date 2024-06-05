package roomescape.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AdminReservationRequest(
        @NotNull
        LocalDate date,
        @NotNull
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        Long themeId,
        @NotNull
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        Long timeId,
        @NotNull
        @Positive(message = "[ERROR] memberId의 값이 1보다 작을 수 없습니다.")
        Long memberId) {
}
