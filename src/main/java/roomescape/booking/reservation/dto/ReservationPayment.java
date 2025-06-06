package roomescape.booking.reservation.dto;

import roomescape.booking.reservation.Reservation;
import roomescape.payment.Payment;

public record ReservationPayment(
        Reservation reservation,
        Payment payment
) {
}
