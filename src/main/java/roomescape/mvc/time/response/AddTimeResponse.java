package roomescape.mvc.time.response;

import java.time.LocalTime;
import roomescape.mvc.time.domain.ReservationTime;

public record AddTimeResponse(Long id, LocalTime startAt) {

    public AddTimeResponse(ReservationTime reservationTime) {
        this(reservationTime.getId(), reservationTime.getStartAt());
    }
}


