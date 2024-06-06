package roomescape.payment.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponse(String paymentKey, int totalAmount) {

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(payment.getPaymentKey(), payment.getTotalAmount());
    }
}
