package roomescape.dto.payment;

import java.math.BigDecimal;
import roomescape.domain.payment.Payment;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        BigDecimal totalAmount
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }

    public Payment toEntity(Long reservationId) {
        return new Payment(
                paymentKey,
                orderId,
                totalAmount,
                reservationId
        );
    }
}
