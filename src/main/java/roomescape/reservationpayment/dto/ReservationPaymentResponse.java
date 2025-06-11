package roomescape.reservationpayment.dto;

import roomescape.reservationpayment.ReservationPayment;

public record ReservationPaymentResponse(
        String paymentKey,
        Long amount
) {

    public static ReservationPaymentResponse from(final ReservationPayment payment) {
        return new ReservationPaymentResponse(payment.getPaymentKey(), payment.getAmount());
    }

    public static ReservationPaymentResponse createEmptyReservationPaymentResponse() {
        return new ReservationPaymentResponse("", 0L);
    }
}
