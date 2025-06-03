package roomescape.payment.dto;

import roomescape.reservation.controller.request.PaymentInfoRequest;

public record TossPaymentRequest(String paymentKey, String orderId, Long amount) {

    public static TossPaymentRequest from(PaymentInfoRequest request) {
        return new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
    }
}
