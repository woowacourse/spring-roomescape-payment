package roomescape.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotEmpty
        String paymentKey,
        @NotEmpty
        String orderId,
        @NotNull
        Long amount) {
}
