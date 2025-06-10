package roomescape.domain.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "예약 가능한 시간 응답 DTO")
public record AvailableReservationTimeResponse(
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,

        @Schema(description = "예약 시작 시간", example = "14:00")
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,

        @Schema(description = "예약이 이미 완료된 시간 여부", example = "false")
        boolean alreadyBooked
) {
}
