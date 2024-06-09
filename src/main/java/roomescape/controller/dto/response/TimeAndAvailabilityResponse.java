package roomescape.controller.dto.response;

import roomescape.domain.reservation.ReservationTime;

public record TimeAndAvailabilityResponse(
        TimeResponse time,
        boolean alreadyBooked
) {
    public static TimeAndAvailabilityResponse from(ReservationTime time, boolean alreadyBooked) {
        return new TimeAndAvailabilityResponse(
                new TimeResponse(time.getId(), time.getStartAt()), alreadyBooked
        );
    }
}
