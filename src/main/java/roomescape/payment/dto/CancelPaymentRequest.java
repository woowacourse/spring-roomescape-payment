package roomescape.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelPaymentRequest(
        @NotBlank
        String paymentKey,

        @NotBlank
        String cancelReason) {
}
