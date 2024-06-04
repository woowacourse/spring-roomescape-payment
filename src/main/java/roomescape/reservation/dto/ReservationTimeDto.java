package roomescape.reservation.dto;

import java.time.LocalTime;
import roomescape.reservation.model.ReservationTime;

public record ReservationTimeDto(
        Long id,
        LocalTime startAt
) {

    public static ReservationTimeDto from(ReservationTime reservationTime) {
        return new ReservationTimeDto(reservationTime.getId(), reservationTime.getStartAt());
    }
}
