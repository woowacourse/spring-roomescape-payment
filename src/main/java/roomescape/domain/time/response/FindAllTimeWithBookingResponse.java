package roomescape.domain.time.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import roomescape.domain.time.dto.ReservationTimeWithBookState;

public record FindAllTimeWithBookingResponse(
        long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,
        boolean alreadyBooked
) {

    public FindAllTimeWithBookingResponse(ReservationTimeWithBookState timeWithBookState) {
        this(timeWithBookState.id(), timeWithBookState.startAt(), timeWithBookState.alreadyBooked());
    }
}
