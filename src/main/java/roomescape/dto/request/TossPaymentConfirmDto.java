package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TossPaymentConfirmDto(
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull Long amount
) {
}
