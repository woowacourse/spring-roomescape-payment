package roomescape.dto;

import java.math.BigDecimal;
import roomescape.domain.Payment;

public record PaymentResponse(
        long id,
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
