package roomescape.application.payment.dto;

import roomescape.domain.payment.TossPayment;

public record TossPaymentCommand(
        String paymentKey,
        String orderId,
        Long amount
) {

    public TossPaymentValidationCommand toValidationCommand() {
        return new TossPaymentValidationCommand(orderId, amount);
    }

    public TossPayment toDomain() {
        return TossPayment.init(paymentKey, orderId, amount);
    }
}
