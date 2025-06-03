package roomescape.payment.application.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(Long paymentId) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId());
    }
}
