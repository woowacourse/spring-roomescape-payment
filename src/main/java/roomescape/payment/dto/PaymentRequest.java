package roomescape.payment.dto;

import roomescape.reservation.controller.request.ReservePaymentRequest;

public record PaymentRequest(String paymentKey, String orderId, Long amount) {

    public static PaymentRequest from(ReservePaymentRequest request) {
        return new PaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
    }
}
