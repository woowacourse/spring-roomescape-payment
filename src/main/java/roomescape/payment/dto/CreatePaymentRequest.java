package roomescape.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(
        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull
        Long amount) {
}
