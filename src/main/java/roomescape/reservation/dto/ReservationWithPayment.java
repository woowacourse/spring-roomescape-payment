package roomescape.reservation.dto;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

public record ReservationWithPayment(Reservation reservation, Payment payment) {
}
