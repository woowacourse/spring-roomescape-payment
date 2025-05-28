package roomescape.payment.infrastructure.dto;

import roomescape.payment.application.dto.PaymentRequest;

public record TossPaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {

    public static TossPaymentRequest from(final PaymentRequest request) {
        return new TossPaymentRequest(request.amount(), request.orderId(), request.paymentKey());
    }
}
