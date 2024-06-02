package roomescape.service.dto;

import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;

public record PaymentApproveRequest(
        Long reservationId,
        Long memberId,
        String paymentKey,
        String orderId,
        Integer amount
) {
    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(amount, paymentKey, orderId);
    }

    public Payment toPayment(PaymentStatus status) {
        return new Payment(reservationId, memberId, paymentKey, orderId, amount, status);
    }
}
