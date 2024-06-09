package roomescape.reservation.dto.request;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Schema(name = "예약 시간 저장 요청", description = "예약 시간 저장 요청시 사용됩니다.")
public record ReservationTimeRequest(
        @NotNull(message = "예약 시간은 null일 수 없습니다.")
        @Schema(description = "예약 시간. HH:mm 형식으로 입력해야 합니다.", type = "string", example = "09:00")
        LocalTime startAt
) {

    public ReservationTimeRequest {
        if (StringUtils.isBlank(startAt.toString())) {
            throw new RoomEscapeException(ErrorType.REQUEST_DATA_BLANK,
                    String.format("[values: %s]", this), HttpStatus.BAD_REQUEST);
        }
    }

    public ReservationTime toTime() {
        return new ReservationTime(this.startAt);
    }
}
