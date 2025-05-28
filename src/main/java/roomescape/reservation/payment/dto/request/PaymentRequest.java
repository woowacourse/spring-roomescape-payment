package roomescape.reservation.payment.dto.request;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount
) {
}
