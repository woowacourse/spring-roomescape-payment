package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull String paymentType
) {
}
