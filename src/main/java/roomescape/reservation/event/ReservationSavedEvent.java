package roomescape.reservation.event;

import roomescape.payment.domain.ConfirmedPayment;
import roomescape.reservation.domain.Reservation;

public record ReservationSavedEvent(Reservation reservation, ConfirmedPayment confirmedPayment) {
}
