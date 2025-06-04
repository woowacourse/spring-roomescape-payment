package roomescape.booking.reservation.reservationpayment.dto;

import roomescape.booking.reservation.reservationpayment.ReservationPayment;

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
