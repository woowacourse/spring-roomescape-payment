package roomescape.dto.payment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PaymentConfirmResponse(@NotEmpty String orderId, @NotEmpty String paymentKey, @NotNull Long totalAmount,
                                     @NotEmpty String status) {
}
