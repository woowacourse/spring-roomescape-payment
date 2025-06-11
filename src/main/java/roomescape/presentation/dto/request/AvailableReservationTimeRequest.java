package roomescape.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "예약 가능 시간 조회 요청 DTO")
public record AvailableReservationTimeRequest(
        @Schema(description = "조회할 날짜", example = "2024-03-20")
        @NotNull(message = "날짜는 필수 선택 사항입니다.")
        LocalDate date,

        @Schema(description = "조회할 테마 ID", example = "1")
        @NotNull(message = "테마는 필수 선택 사항입니다.")
        Long themeId
) {
}
