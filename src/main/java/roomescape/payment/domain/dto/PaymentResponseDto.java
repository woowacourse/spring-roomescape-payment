package roomescape.payment.domain.dto;

import roomescape.payment.domain.TossPayment;

public record PaymentResponseDto(String orderId, int amount, String paymentKey) {

    public static PaymentResponseDto of(TossPayment payment) {
        return new PaymentResponseDto(payment.getOrderId(), payment.getAmount(), payment.getPaymentKey());
    }
}
