package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull String orderId,
        @NotNull String paymentKey,
        @NotNull Long amount,
        @NotNull String paymentType
) {
}
