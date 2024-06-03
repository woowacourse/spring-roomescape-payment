package roomescape.dto.service;

import java.math.BigDecimal;
import roomescape.domain.Payment;

public record TossPaymentResponse(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
    public Payment toPayment() {
        return new Payment(null, paymentKey, orderId, totalAmount);
    }
}
