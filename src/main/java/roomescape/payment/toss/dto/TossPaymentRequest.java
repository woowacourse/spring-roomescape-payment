package roomescape.payment.toss.dto;

import roomescape.payment.toss.domain.TossPayment;
import roomescape.reservation.controller.request.ReservePaymentRequest;

public record TossPaymentRequest(String paymentKey, String orderId, Long amount) {

    public static TossPaymentRequest from(TossPayment tossPayment) {
        return new TossPaymentRequest(
                tossPayment.getPaymentKey(),
                tossPayment.getOrderId(),
                tossPayment.getAmount()
        );
    }

    public static TossPaymentRequest from(ReservePaymentRequest request) {
        return new TossPaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        );
    }
}
