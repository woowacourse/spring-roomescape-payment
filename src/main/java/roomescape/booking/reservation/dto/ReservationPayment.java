package roomescape.booking.reservation.dto;

import roomescape.booking.reservation.Reservation;
import roomescape.booking.reservation.ReservationPaymentStatus;
import roomescape.payment.Payment;

public record ReservationPayment(
        Reservation reservation,
        Payment payment
) {

    public ReservationPaymentStatus paymentStatus() {
        if (payment == null) {
            return ReservationPaymentStatus.PENDING;
        }
        return ReservationPaymentStatus.COMPLETED;
    }
}
