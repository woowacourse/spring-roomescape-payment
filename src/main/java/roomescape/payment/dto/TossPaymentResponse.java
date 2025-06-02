package roomescape.payment.dto;

import roomescape.payment.domain.PaymentType;

public record TossPaymentResponse(
        String orderId,
        String paymentKey,
        long totalAmount,
        PaymentType type
) {
}
