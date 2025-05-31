package roomescape.reservation.external.toss.dto;

import roomescape.reservation.domain.PaymentInfo;

public record PaymentConfirmRequest(
        String paymentKey,
        Long amount,
        String orderId
) {
    public static PaymentConfirmRequest from(PaymentInfo info) {
        return new PaymentConfirmRequest(
                info.paymentKeyAsString(),
                info.totalAmountAsLong(),
                info.orderIdAsString()
        );
    }
}
