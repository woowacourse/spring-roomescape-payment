package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationPaymentRequest(
        @NotNull LocalDate date,
        @NotNull Long themeId,
        @NotNull Long timeId,
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull String paymentType
) {
    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(paymentKey, orderId, amount, paymentType);
    }
}
