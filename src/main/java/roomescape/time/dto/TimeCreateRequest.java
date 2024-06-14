package roomescape.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.time.domain.ReservationTime;

public record TimeCreateRequest(
        @JsonFormat(pattern = "HH:mm")
        @Schema(description = "예약 시간", type = "String", pattern = "HH:mm", example = "23:00")
        LocalTime startAt) {
    public ReservationTime createReservationTime() {
        return new ReservationTime(startAt);
    }
}
