package roomescape.payment.dto;

import roomescape.payment.domain.Payment;

public record PaymentSaveResponse(String paymentKey, String status, int amount) {

    public static PaymentSaveResponse toResponse(Payment payment) {
        return new PaymentSaveResponse(payment.getPaymentKey(), payment.getStatus(), payment.getTotalAmount());
    }
}
