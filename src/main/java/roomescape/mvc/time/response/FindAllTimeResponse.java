package roomescape.mvc.time.response;

import java.time.LocalTime;
import roomescape.mvc.time.domain.ReservationTime;

public record FindAllTimeResponse(Long id, LocalTime startAt) {

    public FindAllTimeResponse(ReservationTime reservationTime) {
        this(reservationTime.getId(), reservationTime.getStartAt());
    }
}

