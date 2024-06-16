package roomescape.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record WaitingRequest(
        @Nonnull
        @DateTimeFormat
        LocalDate date,
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        long timeId,
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        long themeId) {
}
