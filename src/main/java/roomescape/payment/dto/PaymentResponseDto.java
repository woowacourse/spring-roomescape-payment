package roomescape.payment.dto;

import roomescape.payment.domain.Payment;

public record PaymentResponseDto(String paymentKey, String orderId, int totalAmount) {

    public static PaymentResponseDto of(Payment payment) {
        return new PaymentResponseDto(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
