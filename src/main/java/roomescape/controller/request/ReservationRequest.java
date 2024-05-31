package roomescape.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull
        @DateTimeFormat
        LocalDate date,
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        long timeId,
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        long themeId,
        String orderId,
        String paymentKey,
        long amount) {
}
