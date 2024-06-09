package roomescape.application.dto.request.time;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import roomescape.domain.reservationdetail.ReservationTime;

@Schema(name = "예약 시간 정보")
public record ReservationTimeRequest(
        @Schema(description = "예약 시간", example = "10:00")
        @NotNull(message = "시간은 빈값을 허용하지 않습니다.")
        LocalTime startAt
) {

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }

    @Override
    @JsonFormat(shape = Shape.STRING, pattern = "HH:mm")
    public LocalTime startAt() {
        return startAt;
    }
}
