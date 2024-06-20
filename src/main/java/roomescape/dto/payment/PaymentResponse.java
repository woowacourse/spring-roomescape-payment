package roomescape.dto.payment;

import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;

public record PaymentResponse(long id, String paymentKey, String orderId, long amount, PaymentStatus status) {
    public static PaymentResponse from(final Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}
