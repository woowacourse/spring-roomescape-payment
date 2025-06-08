package roomescape.reservation.external.toss.dto;

import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.domain.PaymentKey;

public record PaymentConfirmResponse(
        String paymentKey,
        Long amount,
        String orderId
) {
    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(
                new OrderId(orderId),
                new Amount(amount),
                new PaymentKey(paymentKey)
        );
    }
}
