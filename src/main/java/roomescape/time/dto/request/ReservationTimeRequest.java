package roomescape.time.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import roomescape.time.domain.ReservationTime;

public record ReservationTimeRequest(
        @JsonFormat(pattern = "HH:mm") LocalTime startAt
) {

    public ReservationTime toEntity() {
        return new ReservationTime(startAt);
    }
}
