package roomescape.application.response;

import roomescape.domain.payment.Payment;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        String orderName,
        long totalAmount
) {
    public static PaymentResponse fromPayment(final Payment payment) {
        return new PaymentResponse(
                payment.paymentKey(),
                payment.orderId(),
                payment.orderName(),
                payment.amount()
        );
    }
}
