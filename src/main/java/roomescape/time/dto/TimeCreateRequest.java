package roomescape.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.time.domain.ReservationTime;

public record TimeCreateRequest(
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "String", pattern = "HH:mm")
        LocalTime startAt) {
    public ReservationTime createReservationTime() {
        return new ReservationTime(startAt);
    }
}
