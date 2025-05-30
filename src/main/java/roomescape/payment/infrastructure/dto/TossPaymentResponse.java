package roomescape.payment.infrastructure.dto;

import roomescape.payment.application.dto.PaymentResponse;

public record TossPaymentResponse(
        String orderId,
        String paymentKey,
        Long totalAmount
) implements PaymentResponse {
}
