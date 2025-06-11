package roomescape.application.payment.query.dto;

import roomescape.domain.payment.ReservationPayment;

public record ReservationPaymentResponse(
        String paymentKey,
        Long amount
) {
    public static ReservationPaymentResponse from(ReservationPayment reservationPayment) {
        return new ReservationPaymentResponse(
                reservationPayment.getPayment().getPaymentKey(),
                reservationPayment.getPayment().getAmount()
        );
    }
}
