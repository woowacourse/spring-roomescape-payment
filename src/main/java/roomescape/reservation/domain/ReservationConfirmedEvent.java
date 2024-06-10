package roomescape.reservation.domain;

import roomescape.payment.domain.PaymentInfo;
import roomescape.reservation.entity.Reservation;

public record ReservationConfirmedEvent(Reservation reservation, PaymentInfo paymentInfo) {
}
