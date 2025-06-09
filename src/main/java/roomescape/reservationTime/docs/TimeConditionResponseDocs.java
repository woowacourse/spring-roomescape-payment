package roomescape.reservationTime.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservationTime.dto.response.TimeConditionResponse;
import java.time.LocalTime;

@Schema(description = "시간 조건 응답")
public record TimeConditionResponseDocs(
        @Schema(description = "시간 ID", example = "1")
        Long id,
        @Schema(description = "시작 시간", example = "10:00")
        LocalTime startAt,
        @Schema(description = "예약 여부", example = "false")
        boolean alreadyBooked) {

    public static TimeConditionResponseDocs from(TimeConditionResponse response) {
        return new TimeConditionResponseDocs(response.id(), response.startAt(), response.alreadyBooked());
    }
} 