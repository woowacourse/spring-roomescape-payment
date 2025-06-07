package roomescape.dto.response;

import roomescape.domain.Payment;

public record PaymentResponse(String orderId, String paymentKey, int totalAmount) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getOrderId(), payment.getPaymentKey(), payment.getAmount());
    }
}
