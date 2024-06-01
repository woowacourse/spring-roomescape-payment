package roomescape.dto.reservationtime;

import java.time.LocalTime;
import roomescape.domain.time.ReservationTime;

public record TimeWithAvailableResponse(
        Long id,
        LocalTime startAt,
        boolean alreadyBooked
) {

    public static TimeWithAvailableResponse from(ReservationTime reservationTime, boolean alreadyBooked) {
        return new TimeWithAvailableResponse(
                reservationTime.getId(),
                reservationTime.getStartAt(),
                alreadyBooked
        );
    }
}
