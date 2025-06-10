package roomescape.domain.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "예약 가능한 시간 DTO")
public record AvailableReservationTime(
        @Schema(description = "시간 ID", example = "1")
        Long id,

        @Schema(description = "예약 시작 시간", example = "14:00")
        LocalTime startAt,

        @Schema(description = "예약이 이미 완료된 시간 여부", example = "false")
        boolean alreadyBooked
) {
}
