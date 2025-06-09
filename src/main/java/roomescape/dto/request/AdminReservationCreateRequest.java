package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "관리자 예약 생성 요청 객체")
public record AdminReservationCreateRequest(
        @NotNull
        @Schema(description = "멤버 ID", example = "1")
        Long memberId,

        @Schema(description = "날짜", example = "2026-08-05")
        @NotNull
        LocalDate date,

        @Schema(description = "예약시간 ID", example = "1")
        @NotNull
        Long timeId,

        @Schema(description = "테마 ID", example = "1")
        @NotNull
        Long themeId
) {
}
