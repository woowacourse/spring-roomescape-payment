package roomescape.payment.infrastructure.dto.response;

import roomescape.payment.infrastructure.dto.PaymentFailure;

public record ConfirmPaymentResponse(
        Integer totalAmount,
        String paymentKey,
        PaymentFailure failure
) {
}
