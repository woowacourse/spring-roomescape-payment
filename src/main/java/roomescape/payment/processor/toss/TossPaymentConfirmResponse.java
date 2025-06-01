package roomescape.payment.processor.toss;

import roomescape.payment.processor.PaymentConfirmResponse;

public record TossPaymentConfirmResponse(String orderId, String paymentKey)
        implements PaymentConfirmResponse {
}