package roomescape.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "예약 시간 생성 시 요청 객체")
public record ReservationTimeRequest(
        @Schema(description = "예약 시작 시간 (HH:mm 형식)", example = "14:30", requiredMode = REQUIRED)
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime startAt
) {
}
