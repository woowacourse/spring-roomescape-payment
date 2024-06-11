package roomescape.dto;

import roomescape.domain.Payment;

import java.math.BigDecimal;

public record TossPaymentRequest(String paymentKey, String orderId, BigDecimal amount) {

    public static TossPaymentRequest from(Payment payment) {
        return new TossPaymentRequest(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
