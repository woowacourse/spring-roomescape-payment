package roomescape.payment.global.domain.dto;

import roomescape.payment.global.domain.Payment;

public record PaymentResponseDto(String orderId, int amount, String paymentKey) {

    public static PaymentResponseDto of(Payment payment) {
        return new PaymentResponseDto(payment.getOrderId(), payment.getAmount(), payment.getPaymentKey());
    }
}
