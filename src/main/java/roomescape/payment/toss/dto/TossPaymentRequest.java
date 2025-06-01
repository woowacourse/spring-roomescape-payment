package roomescape.payment.toss.dto;

import roomescape.reservation.controller.request.PaymentInfoRequest;
import roomescape.reservation.controller.request.ReservePaymentRequest;

public record TossPaymentRequest(String paymentKey, String orderId, Long amount) {

    public static TossPaymentRequest from(ReservePaymentRequest request) {
        return new TossPaymentRequest(
                request.payment().paymentKey(),
                request.payment().orderId(),
                request.payment().amount()
        );
    }

    public static TossPaymentRequest from(PaymentInfoRequest paymentInfoRequest) {
        return new TossPaymentRequest(paymentInfoRequest.paymentKey(), paymentInfoRequest.orderId(),
                paymentInfoRequest.amount());
    }
}
