package roomescape.reservationtime.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationtime.domain.ReservationTime;

public record ReservationTimeCreateWebRequest(
        @JsonFormat(pattern = "HH:mm") @Schema(description = "예약 가능한 시간") LocalTime startAt
) {
    public ReservationTimeCreateWebRequest {
        if (startAt == null) {
            throw new IllegalArgumentException("시작 시간은 null일 수 없습니다.");
        }
    }

    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
