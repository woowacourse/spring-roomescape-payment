package roomescape.reservation.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationPaymentSaveRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount
) {
}
