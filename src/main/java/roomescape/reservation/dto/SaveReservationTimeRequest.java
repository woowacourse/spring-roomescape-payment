package roomescape.reservation.dto;

import java.time.LocalTime;
import roomescape.reservation.model.ReservationTime;

public record SaveReservationTimeRequest(LocalTime startAt) {

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
