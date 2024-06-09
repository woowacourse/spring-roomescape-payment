package roomescape.reservation.event;

import roomescape.payment.domain.ConfirmedPayment;

public record ReservationFailedEvent(ConfirmedPayment confirmedPayment) {
}
