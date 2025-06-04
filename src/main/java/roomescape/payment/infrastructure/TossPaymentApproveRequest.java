package roomescape.payment.infrastructure;

import roomescape.payment.domain.PaymentInfo;

public record TossPaymentApproveRequest(
        String paymentKey,
        String orderId,
        long amount
) {
    public static TossPaymentApproveRequest from(final PaymentInfo paymentInfo) {
        return new TossPaymentApproveRequest(
                paymentInfo.getPaymentKey(),
                paymentInfo.getOrderId(),
                paymentInfo.getAmount()
        );
    }
}
