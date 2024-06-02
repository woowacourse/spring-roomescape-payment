package roomescape.reservation.dto.request;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

public record ReservationTimeRequest(
        @NotNull(message = "예약 시간은 null일 수 없습니다.")
        @DateTimeFormat(pattern = "kk:mm")
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
