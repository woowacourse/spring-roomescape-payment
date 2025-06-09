package roomescape.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "예약 조건 요청")
public record ReservationConditionRequest(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "시작 날짜", example = "2024-03-20")
        LocalDate dateFrom,
        @Schema(description = "종료 날짜", example = "2024-03-25")
        LocalDate dateTo) {

    public boolean isEmpty() {
        return memberId == null && themeId == null && dateFrom == null && dateTo == null;
    }
}
