package roomescape.payment.infra.toss.dto;

import roomescape.payment.dto.PaymentRequest;

public record TossPaymentRequest(String paymentKey, String orderId, Long amount) {

    public static TossPaymentRequest from(PaymentRequest request) {
        return new TossPaymentRequest(request.paymentKey(), request.orderId(), request.amount());
    }
}
