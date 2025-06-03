package roomescape.payment.domain.dto;

import roomescape.payment.domain.TossPayment;

public record PaymentResponseDto(String paymentKey, String orderId, int amount) {

    public static PaymentResponseDto of(TossPayment payment) {
        return new PaymentResponseDto(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
