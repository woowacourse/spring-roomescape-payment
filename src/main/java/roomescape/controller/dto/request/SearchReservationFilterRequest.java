package roomescape.controller.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchReservationFilterRequest(
        @Schema(description = "테마 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long themeId,

        @Schema(description = "회원 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long memberId,

        @Schema(description = "조회하고 싶은 예약 범위 중 시작 날짜", example = "2024-06-06")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateFrom,

        @Schema(description = "조회하고 싶은 예약 범위 중 종료 날짜", example = "2024-06-08")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateTo
) {
}
