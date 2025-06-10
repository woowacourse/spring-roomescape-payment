package roomescape.dto.reservation;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

public record ReservationWithPayment(Reservation reservation, Payment payment) {
}
