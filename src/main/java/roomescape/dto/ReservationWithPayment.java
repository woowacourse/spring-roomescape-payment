package roomescape.dto;

import roomescape.domain.Payment;
import roomescape.domain.Reservation;

public record ReservationWithPayment(Reservation reservation, Payment payment) {
}
