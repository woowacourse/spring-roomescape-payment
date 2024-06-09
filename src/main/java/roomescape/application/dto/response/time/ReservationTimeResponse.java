package roomescape.application.dto.response.time;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import roomescape.domain.reservationdetail.ReservationTime;

@Schema(name = "예약 시간 정보")
public record ReservationTimeResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        Long id,

        @Schema(description = "시작 시간", example = "10:00:00", type = "string", format = "time")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime startAt
) {

    public static ReservationTimeResponse from(ReservationTime time) {
        return new ReservationTimeResponse(
                time.getId(),
                time.getStartAt()
        );
    }

    @Override
    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime startAt() {
        return startAt;
    }
}
