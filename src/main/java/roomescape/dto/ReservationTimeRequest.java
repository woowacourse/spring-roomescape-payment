package roomescape.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.entity.ReservationTime;

@Schema(description = "예약 시간 요청 DTO 입니다.")
public record ReservationTimeRequest(@Schema(description = "예약 시간입니다.") LocalTime startAt) {
    public ReservationTime toReservationTime() {
        return new ReservationTime(startAt);
    }
}
