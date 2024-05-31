package roomescape.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record WaitingRequest(
        @NotNull
        @DateTimeFormat
        LocalDate date,
        @NotNull
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        Long timeId,
        @NotNull
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        Long themeId) {
}
