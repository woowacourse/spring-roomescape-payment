package roomescape.reservation.repository.dto;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record ReservationWithPayment(
        Reservation reservation,
        Payment payment
) {
}
