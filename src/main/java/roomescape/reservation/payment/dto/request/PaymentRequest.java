package roomescape.reservation.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import roomescape.reservation.payment.domain.PaymentMethod;

public record PaymentRequest(
        @NotNull String paymentKey,
        @NotNull String orderId,
        @NotNull Long amount,
        @NotNull PaymentMethod method
) {
}
