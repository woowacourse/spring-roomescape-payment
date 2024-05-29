package roomescape.reservation.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import roomescape.reservation.domain.ReservationTime;

@NotNull
public record TimeSaveRequest(
        @JsonFormat(pattern = "HH:mm") LocalTime startAt
) {

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
