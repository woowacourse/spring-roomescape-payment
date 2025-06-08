package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "관리자용 예약 생성 요청 DTO")
public record AdminReservationCreateRequest(
        @Schema(description = "예약 일자", example = "2024-03-20")
        @NotNull(message = "예약 일자는 필수입니다.")
        LocalDate date,

        @Schema(description = "예약 시간 ID", example = "1")
        @NotNull(message = "예약 시간은 필수입니다.")
        Long timeId,

        @Schema(description = "예약 테마 ID", example = "1")
        @NotNull(message = "예약 테마는 필수입니다.")
        Long themeId,

        @Schema(description = "예약자 회원 ID", example = "1")
        @NotNull(message = "예약자 선택은 필수입니다.")
        Long memberId
) {
}
