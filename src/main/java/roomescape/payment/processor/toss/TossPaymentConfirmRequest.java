package roomescape.payment.processor.toss;

import roomescape.payment.dto.PaymentConfirmRequest;

public record TossPaymentConfirmRequest(int amount, String orderId, String paymentKey) implements
        PaymentConfirmRequest {
}
