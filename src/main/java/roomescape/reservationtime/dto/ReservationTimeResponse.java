package roomescape.reservationtime.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalTime;

@Tag(name = "예약 시간 응답", description = "예약 시간 응답")
public record ReservationTimeResponse(long id, LocalTime startAt) {
}
