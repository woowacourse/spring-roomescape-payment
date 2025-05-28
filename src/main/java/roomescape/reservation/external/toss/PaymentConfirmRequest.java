package roomescape.reservation.external.toss;

import roomescape.reservation.domain.PaymentInfo;

public record PaymentConfirmRequest(
        String paymentKey,
        Long amount,
        String orderId
) {
    public static PaymentConfirmRequest from(PaymentInfo info) {
        return new PaymentConfirmRequest(info.paymentKey(), info.totalAmount(), info.orderId());
    }
}
