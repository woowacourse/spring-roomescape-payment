package roomescape.infrastructure.dto.response;

import roomescape.infrastructure.dto.PaymentFailure;

public record ConfirmPaymentResponse(
        Integer totalAmount,
        String paymentKey,
        PaymentFailure failure
) {
}
