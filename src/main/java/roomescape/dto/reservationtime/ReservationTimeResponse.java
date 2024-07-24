package roomescape.dto.reservationtime;

import java.time.LocalTime;
import roomescape.domain.time.ReservationTime;

public record ReservationTimeResponse(
        Long id,
        LocalTime startAt
) {

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(
                reservationTime.getId(),
                reservationTime.getStartAt()
        );
    }
}
