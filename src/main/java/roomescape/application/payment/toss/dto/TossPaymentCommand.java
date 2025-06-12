package roomescape.application.payment.toss.dto;

import roomescape.domain.payment.TossPayment;

public record TossPaymentCommand(
        Long paymentId,
        String paymentKey,
        String orderId,
        Long amount
) {

    public TossPaymentValidationCommand toValidationCommand() {
        return new TossPaymentValidationCommand(orderId, amount);
    }

    public TossPayment toDomain() {
        return TossPayment.init(paymentId, paymentKey, orderId, amount);
    }
}
