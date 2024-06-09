package roomescape.dto.payment;

import roomescape.domain.payment.Payment;

public record PaymentCancelRequest(
        String paymentKey,
        String cancelReason
) {

    public static PaymentCancelRequest of(Payment payment, String cancelReason) {
        return new PaymentCancelRequest(payment.getPaymentKey(), cancelReason);
    }
}
