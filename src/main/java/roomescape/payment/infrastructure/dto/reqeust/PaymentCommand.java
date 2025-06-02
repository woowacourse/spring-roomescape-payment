package roomescape.payment.infrastructure.dto.reqeust;

import roomescape.reservation.dto.request.PaymentRequest;

public record PaymentCommand(String paymentKey, String orderId, Integer amount, String paymentType) {

    public static PaymentCommand createByPaymentRequest(PaymentRequest request) {
        return new PaymentCommand(request.paymentKey(), request.orderId(), request.amount(), request.paymentType());
    }
}
