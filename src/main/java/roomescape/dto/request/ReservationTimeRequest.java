package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import roomescape.domain.ReservationTime;

@Schema(description = "예약 시간 생성 요청 객체")
public record ReservationTimeRequest(
        @NotNull
        @Schema(description = "시간 시간", example = "10:00")
        LocalTime startAt
) {
    public ReservationTime toTime() {
        return ReservationTime.createWithoutId(startAt);
    }
}
