package roomescape.dto.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {
    public static PaymentResponse from(final Payment payment) {
        return new PaymentResponse(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }

    public Payment toEntity() {
        return new Payment(orderId, totalAmount, paymentKey);
    }
}
