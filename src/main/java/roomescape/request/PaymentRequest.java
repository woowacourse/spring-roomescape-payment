package roomescape.request;

import jakarta.annotation.Nonnull;

public record PaymentRequest(
        @Nonnull
        String paymentKey,
        @Nonnull
        String orderId,
        Long amount) {
}
