package roomescape.reservation.model;

import roomescape.payment.model.Payment;

public record ReservationWithPayment(Reservation reservation,
                                     Payment payment) {
}
