package roomescape.reservation.dto.response;

import java.time.LocalTime;

public record ReservationTimeInfoResponse(
        Long timeId,
        LocalTime startAt,
        boolean alreadyBooked
) {
}
