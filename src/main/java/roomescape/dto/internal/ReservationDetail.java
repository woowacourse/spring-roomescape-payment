package roomescape.dto.internal;

import roomescape.entity.Payment;
import roomescape.entity.Reservation;

public record ReservationDetail(
        Reservation reservation,
        Payment payment,
        long rank
) {
}
