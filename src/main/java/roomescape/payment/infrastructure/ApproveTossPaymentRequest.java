package roomescape.payment.infrastructure;

import roomescape.payment.domain.PaymentInfo;

public record ApproveTossPaymentRequest(
        String paymentKey,
        String orderId,
        long amount
) {
    public static ApproveTossPaymentRequest from(final PaymentInfo paymentInfo) {
        return new ApproveTossPaymentRequest(
                paymentInfo.getPaymentKey(),
                paymentInfo.getOrderId(),
                paymentInfo.getAmount()
        );
    }
}
