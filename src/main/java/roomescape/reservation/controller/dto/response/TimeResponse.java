package roomescape.reservation.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationTime;

public record TimeResponse(
        long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt
) {

    public static TimeResponse toResponse(ReservationTime reservationTime) {
        return new TimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
