package roomescape.reservation.event;

import roomescape.payment.domain.Payment;

public record ReservationSavedEvent(Payment payment) {
}
