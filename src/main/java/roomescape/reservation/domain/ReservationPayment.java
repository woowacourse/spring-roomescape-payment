package roomescape.reservation.domain;

import roomescape.payment.domain.Payment;

public record ReservationPayment(Reservation reservation, Payment payment) {
}
