package roomescape.reservationtime.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalTime;

@Tag(name = "예약 시간 요청", description = "사용자가 요청하는 예약 시간")
public record ReservationTimeRequest(LocalTime startAt) {
}
