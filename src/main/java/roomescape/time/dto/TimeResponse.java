package roomescape.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.time.domain.ReservationTime;

public record TimeResponse(
        @Schema(description = "예약 시간 id", example = "1")
        Long id,
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "예약 시간", type = "String", pattern = "HH:mm", example = "23:00")
        LocalTime startAt) {
    public static TimeResponse from(ReservationTime time) {
        return new TimeResponse(time.getId(), time.getStartAt());
    }
}
