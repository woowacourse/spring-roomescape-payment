package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(name = "관리자 예약 저장 요청", description = "관리자의 예약 저장 요청시 사용됩니다.")
public record AdminReservationRequest(
        @Schema(description = "예약 날짜. 지난 날짜는 지정할 수 없으며, yyyy-MM-dd 형식으로 입력해야 합니다.", type = "string", example = "2022-12-31")
        LocalDate date,
        @Schema(description = "예약 시간 ID.", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "회원 ID", example = "1")
        Long memberId
) {
}
