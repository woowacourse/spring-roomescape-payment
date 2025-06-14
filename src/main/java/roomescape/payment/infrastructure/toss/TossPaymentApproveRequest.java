package roomescape.payment.infrastructure.toss;

import roomescape.payment.domain.Payment;

public record TossPaymentApproveRequest(
        String paymentKey,
        String orderId,
        long amount
) {
    public static TossPaymentApproveRequest from(final Payment payment) {
        return new TossPaymentApproveRequest(
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }
}
