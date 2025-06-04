package roomescape.reservationTime.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.reservationTime.domain.ReservationTime;

@Schema(description = "예약 가능 시간 응답")
public record AvailableTimeResponse(
        @Schema(description = "예약 시간 ID")
        Long timeId,

        @Schema(description = "예약 시작 시간", pattern =  "HH:mm")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt,

        @Schema(description = "이미 예약된 시간인지 여부")
        boolean alreadyBooked
) {
    public static AvailableTimeResponse of(ReservationTime time, boolean alreadyBooked) {
        return new AvailableTimeResponse(time.getId(), time.getStartAt(), alreadyBooked);
    }
}
