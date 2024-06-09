package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(name = "예약 대기 저장 요청", description = "회원의 예약 대기 요청시 사용됩니다.")
public record WaitingRequest(
        @NotNull(message = "예약 날짜는 null일 수 없습니다.")
        @Schema(description = "예약 날짜. yyyy-MM-dd 형식으로 입력해야 합니다.", type = "string", example = "2022-12-31")
        LocalDate date,
        @NotNull(message = "예약 요청의 timeId는 null일 수 없습니다.")
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,
        @NotNull(message = "예약 요청의 themeId는 null일 수 없습니다.")
        @Schema(description = "테마 ID", example = "1")
        Long themeId
) {
}
