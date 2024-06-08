package roomescape.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ReservationRequest(
        @Nonnull
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        long timeId,
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        long themeId,
        String orderId,
        String paymentKey,
        long amount) {
}
