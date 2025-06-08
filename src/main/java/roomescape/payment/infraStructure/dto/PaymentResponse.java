package roomescape.payment.infraStructure.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(
        int amount,
        String paymentKey
) {
    public static PaymentResponse fromEntity(Payment payment) {
        return new PaymentResponse(payment.getAmount(), payment.getPaymentKey());
    }
}
