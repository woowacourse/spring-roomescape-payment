package roomescape.presentation.dto.response;

import roomescape.business.model.entity.Payment;

public record PaymentResponse(
        String id,
        String paymentKey,
        Long amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId().value(), payment.getPaymentKey(), payment.getAmount());
    }
}
