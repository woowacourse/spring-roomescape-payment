package roomescape.reservation.external.toss;

import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.domain.TossPayment;

public record TossPaymentResponse(
        String orderId,
        Long totalAmount,
        String paymentKey
) {
    public TossPayment toEntity() {
        return new TossPayment(new OrderId(orderId), new Amount(totalAmount), new PaymentKey(paymentKey));
    }
}
