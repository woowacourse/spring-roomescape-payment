package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Schema(description = "예약 시간 생성 요청 DTO")
public record ReservationTimeCreateRequest(
        @Schema(description = "예약 시작 시간", example = "14:00")
        @NotNull(message = "예약 시간은 필수입니다.")
        LocalTime startAt
) {
}
