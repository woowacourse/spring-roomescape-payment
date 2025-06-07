package roomescape.presentation.dto.response;

import roomescape.domain.Payment;

public record PaymentResponse(
        Long id,
        String paymentKey,
        String orderId,
        int totalAmount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getTotalAmount()
        );
    }
}
