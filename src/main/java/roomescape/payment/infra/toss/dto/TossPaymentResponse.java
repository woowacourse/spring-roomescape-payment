package roomescape.payment.infra.toss.dto;

import roomescape.payment.dto.PaymentResponse;

public record TossPaymentResponse(String orderId, String paymentKey) {

    public PaymentResponse toPaymentResponse() {
        return new PaymentResponse(orderId, paymentKey);
    }
}
