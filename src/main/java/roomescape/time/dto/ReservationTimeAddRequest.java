package roomescape.time.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import roomescape.time.domain.ReservationTime;

public record ReservationTimeAddRequest(

        @Schema(description = "예약 시간", example = "23:59")
        @NotNull(message = "예약 시간은 필수입니다.")
        LocalTime startAt
) {

    public ReservationTime toReservationTime() {
        return new ReservationTime(null, startAt);
    }
}
