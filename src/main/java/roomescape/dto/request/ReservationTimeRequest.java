package roomescape.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public record ReservationTimeRequest(
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime startAt
) {
}
