package roomescape.dto.internal;

import roomescape.entity.Reservation;

public record ReservationWithRank(
        Reservation reservation,
        long rank
) {
}
