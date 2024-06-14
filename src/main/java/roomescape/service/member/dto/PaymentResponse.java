package roomescape.service.member.dto;

import roomescape.domain.payment.Payment;

public record PaymentResponse(String paymentKey, Long amount) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getPaymentKey(),
            payment.getTotalAmount()
        );
    }
}
