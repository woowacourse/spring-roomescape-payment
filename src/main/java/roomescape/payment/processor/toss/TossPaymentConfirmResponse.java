package roomescape.payment.processor.toss;

import roomescape.payment.dto.PaymentConfirmResponse;

public record TossPaymentConfirmResponse(String orderId, String paymentKey)
        implements PaymentConfirmResponse {
}