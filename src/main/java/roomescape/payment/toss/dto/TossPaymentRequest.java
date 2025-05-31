package roomescape.payment.toss.dto;

import roomescape.reservation.controller.request.ReservePaymentRequest;

public record TossPaymentRequest(String paymentKey, String orderId, Long amount) {

    public static TossPaymentRequest from(ReservePaymentRequest request) {
        return new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
    }
}
