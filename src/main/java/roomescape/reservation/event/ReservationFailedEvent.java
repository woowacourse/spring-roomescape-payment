package roomescape.reservation.event;

import roomescape.payment.domain.Payment;

public record ReservationFailedEvent(Payment payment) {
}
