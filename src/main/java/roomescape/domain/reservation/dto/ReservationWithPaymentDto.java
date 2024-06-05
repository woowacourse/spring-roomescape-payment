package roomescape.domain.reservation.dto;

import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;

public record ReservationWithPaymentDto(Reservation reservation, Payment payment) {
}
