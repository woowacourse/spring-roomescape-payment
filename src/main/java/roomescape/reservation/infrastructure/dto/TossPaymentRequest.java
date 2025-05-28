package roomescape.reservation.infrastructure.dto;

import roomescape.reservation.application.dto.PaymentRequest;

public record TossPaymentRequest(
        Long amount,
        String orderId,
        String paymentKey
) {

    public static TossPaymentRequest from(final PaymentRequest request) {
        return new TossPaymentRequest(request.amount(), request.orderId(), request.paymentKey());
    }
}
