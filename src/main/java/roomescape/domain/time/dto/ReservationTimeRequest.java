package roomescape.domain.time.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

@Schema(description = "예약 시간 요청 DTO")
public record ReservationTimeRequest(
        @Schema(description = "예약 시작 시간", example = "14:00")
        @JsonFormat(pattern = "HH:mm") LocalTime startAt
) {

}
