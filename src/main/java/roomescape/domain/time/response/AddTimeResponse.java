package roomescape.domain.time.response;

import java.time.LocalTime;
import roomescape.domain.time.domain.ReservationTime;

public record AddTimeResponse(Long id, LocalTime startAt) {

    public AddTimeResponse(ReservationTime reservationTime) {
        this(reservationTime.getId(), reservationTime.getStartAt());
    }
}


