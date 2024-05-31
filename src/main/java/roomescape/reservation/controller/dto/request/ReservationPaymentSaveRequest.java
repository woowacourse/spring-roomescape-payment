package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationPaymentSaveRequest(
        @NotNull LocalDate date,
        @NotNull long themeId,
        @NotNull long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull long amount
) {
}
