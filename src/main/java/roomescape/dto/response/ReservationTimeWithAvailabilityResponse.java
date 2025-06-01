package roomescape.dto.response;

import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeWithAvailabilityResponse(
        long id,
        LocalTime startAt,
        boolean isBooked
) {

    public static ReservationTimeWithAvailabilityResponse from(ReservationTime time, boolean isBooked) {
        return new ReservationTimeWithAvailabilityResponse(time.getId(), time.getStartAt(), isBooked);
    }
}
