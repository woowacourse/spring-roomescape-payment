package roomescape.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.ReservationTime;

@Schema(description = "예약 시간 응답 객체")
public record ReservationTimeResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        Long id,

        @Schema(description = "예약 시작 시간", example = "13:30")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startAt
) {
    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.getId(), reservationTime.getStartAt());
    }
}
