package roomescape.controller.request;

import jakarta.validation.constraints.NotEmpty;

public record PaymentRequest(
        @NotEmpty
        String paymentKey,
        @NotEmpty
        String orderId,
        Long amount) {
}
