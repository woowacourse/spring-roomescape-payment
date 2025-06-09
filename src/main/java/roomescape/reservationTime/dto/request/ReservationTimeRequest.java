package roomescape.reservationTime.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "예약 시간 요청")
public record ReservationTimeRequest(
        @Schema(description = "시작 시간", example = "10:00")
        LocalTime startAt) {
    public ReservationTimeRequest {
        if (startAt == null) {
            throw new IllegalArgumentException("시간은 null 일 수 없습니다.");
        }

    }
}
