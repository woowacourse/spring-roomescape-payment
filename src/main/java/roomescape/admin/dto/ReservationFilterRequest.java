package roomescape.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.admin.domain.FilterInfo;

import java.time.LocalDate;

@Schema(description = "예약 필터링 조회 요청 데이터")
public record ReservationFilterRequest(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "테마 ID", example = "1")
        Long themeId,

        @Schema(description = "조회 시작 날짜", example = "2099-01-01")
        LocalDate from,

        @Schema(description = "조회 종료 날짜", example = "2099-12-31")
        LocalDate to) {

    public FilterInfo toFilterInfo() {
        return new FilterInfo(memberId, themeId, from, to);
    }
}
