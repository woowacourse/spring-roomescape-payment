package roomescape.time.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import roomescape.time.domain.ReservationTime;

public record ReservationTimeResponse(
        Long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt
) {

    public static ReservationTimeResponse from(final ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
