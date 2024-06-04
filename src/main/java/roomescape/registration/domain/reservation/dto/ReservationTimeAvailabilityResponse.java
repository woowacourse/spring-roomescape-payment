package roomescape.registration.domain.reservation.dto;

import roomescape.reservationtime.domain.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeAvailabilityResponse(long timeId, LocalTime startAt, boolean alreadyBooked) {

    public static ReservationTimeAvailabilityResponse fromTime(ReservationTime time, boolean alreadyBooked) {
        return new ReservationTimeAvailabilityResponse(time.getId(), time.getStartAt(), alreadyBooked);
    }
}
