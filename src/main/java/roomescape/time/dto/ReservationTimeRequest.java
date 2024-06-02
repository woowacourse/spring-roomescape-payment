package roomescape.time.dto;

import java.time.LocalTime;

import roomescape.time.entity.ReservationTime;

public record ReservationTimeRequest(LocalTime startAt) {
    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
