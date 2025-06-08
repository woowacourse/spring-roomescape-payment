package roomescape.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

public record ReservationTimeRequest(
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt
) {

}
