package roomescape.service.response;

import java.time.LocalTime;

import roomescape.domain.ReservationTime;

public record ReservationTimeDto(Long id, LocalTime startAt) {

    public ReservationTimeDto(ReservationTime reservationTime) {
        this(reservationTime.getId(), reservationTime.getStartAt());
    }
}
