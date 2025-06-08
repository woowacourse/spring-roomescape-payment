package roomescape.application.payment;

import roomescape.domain.reservation.PaymentType;

public record PaymentResult(
        PaymentType paymentType,
        String paymentKey,
        Long amount
) {

}
