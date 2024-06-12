package roomescape.application.dto.response.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;

public record PaymentResponse(
        BigDecimal totalAmount,
        String paymentKey,
        String orderId,
        String requestedAt,
        String approvedAt
) {
    public Payment toPayment() {
        return new Payment(totalAmount, paymentKey, orderId, requestedAt, approvedAt);
    }
}
