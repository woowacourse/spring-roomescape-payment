package roomescape.application.dto.request.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Schema(name = "예약 정보")
public record ReservationRequest(
        @Schema(description = "예약 날짜", example = "2024-10-10")
        @Future(message = "과거에 대한 예약은 할 수 없습니다.")
        @NotNull(message = "날짜에 빈값은 허용하지 않습니다.")
        LocalDate date,

        @Schema(description = "멤버 ID", example = "4")
        @Positive(message = "멤버 아이디는 1이상의 정수만 허용합니다.")
        Long memberId,

        @Schema(description = "예약 시간 ID", example = "1")
        @Positive(message = "타임 아이디는 1이상의 정수만 허용합니다.")
        Long timeId,

        @Schema(description = "테마 ID", example = "1")
        @Positive(message = "테마 아이디는 1이상의 정수만 허용합니다.")
        Long themeId
) {
}
