package roomescape.mvc.time.dto;

import java.time.LocalTime;

public record ReservationTimeWithBookState(
        long id,
        LocalTime startAt,
        boolean alreadyBooked
) {

}
