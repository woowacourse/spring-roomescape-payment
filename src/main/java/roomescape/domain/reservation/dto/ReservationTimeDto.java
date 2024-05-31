package roomescape.domain.reservation.dto;

import roomescape.domain.reservation.model.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeDto(
        Long id,
        LocalTime startAt
) {
    public static ReservationTimeDto from(ReservationTime reservationTime) {
        return new ReservationTimeDto(reservationTime.getId(), reservationTime.getStartAt());
    }
}
