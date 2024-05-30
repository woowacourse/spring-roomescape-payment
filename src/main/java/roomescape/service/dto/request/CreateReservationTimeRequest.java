package roomescape.service.dto.request;

import roomescape.domain.reservationtime.ReservationTime;

import java.time.LocalTime;

public record CreateReservationTimeRequest(LocalTime startAt) {

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
