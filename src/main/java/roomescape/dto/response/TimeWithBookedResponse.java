package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.ReservationTime;

@Schema(description = "예약 시간 및 예약 여부 응답 객체")
public record TimeWithBookedResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        Long id,

        @Schema(description = "예약 시작 시간", example = "14:30")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt,

        @Schema(description = "이미 예약되었는지 여부", example = "true")
        boolean alreadyBooked
) {
    public static TimeWithBookedResponse of(ReservationTime time, boolean alreadyBooked) {
        return new TimeWithBookedResponse(
                time.getId(), time.getStartAt(), alreadyBooked);
    }
}
