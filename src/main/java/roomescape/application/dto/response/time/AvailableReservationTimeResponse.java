package roomescape.application.dto.response.time;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import roomescape.domain.reservationdetail.ReservationTime;

@Schema(name = "예약 시간 정보")
public record AvailableReservationTimeResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        Long id,

        @Schema(description = "시작 시간", example = "10:00:00", type = "string", format = "time")
        LocalTime startAt,

        @Schema(description = "이미 예약된 시간 여부", example = "false")
        boolean alreadyBooked
) {

    public static AvailableReservationTimeResponse of(ReservationTime time, List<ReservationTime> reservedTimes) {
        return new AvailableReservationTimeResponse(
                time.getId(),
                time.getStartAt(),
                time.isAlreadyBooked(reservedTimes)
        );
    }

    @Override
    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime startAt() {
        return startAt;
    }
}
