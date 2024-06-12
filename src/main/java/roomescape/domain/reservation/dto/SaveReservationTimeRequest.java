package roomescape.domain.reservation.dto;

import roomescape.domain.reservation.model.ReservationTime;

import java.time.LocalTime;

public record SaveReservationTimeRequest(LocalTime startAt) {
    public ReservationTime toModel() {
        return new ReservationTime(startAt);
    }
}
