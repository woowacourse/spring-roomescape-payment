package roomescape.payment.controller.dto;

import roomescape.payment.domain.Payment;

public record PaymentWebResponse(
        String paymentKey,
        String orderId,
        int amount
) {

    public static PaymentWebResponse from(final Payment payment) {
        return new PaymentWebResponse(
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }
}
