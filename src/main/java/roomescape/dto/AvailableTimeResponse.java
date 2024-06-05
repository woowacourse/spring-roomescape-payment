package roomescape.dto;

import roomescape.entity.ReservationTime;

import java.time.LocalTime;

public record AvailableTimeResponse(long id, LocalTime startAt, boolean isBooked) {
    public static AvailableTimeResponse of(ReservationTime reservationTime, boolean isReserved) {
        return new AvailableTimeResponse(reservationTime.getId(), reservationTime.getStartAt(), isReserved);
    }
}
