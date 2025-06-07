package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import roomescape.reservation.entity.ReservationTime;

public record ReservationTimeCreateRequest(

        @Schema(description = "예약 시간", example = "15:00", type = "string", pattern = "HH:mm")
        @NotNull LocalTime startAt
) {
    public ReservationTime toEntity() {
        return new ReservationTime(startAt);
    }
}
