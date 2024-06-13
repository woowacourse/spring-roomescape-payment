package roomescape.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "AdminReservationRequest", description = "관리자 예약 추가 요청 데이터")
public record AdminReservationRequest(

        @Schema(description = "예약 날짜", example = "2099-12-31")
        LocalDate date,

        @Schema(description = "테마 ID", example = "1")
        long themeId,

        @Schema(description = "시간 ID", example = "1")
        long timeId,

        @Schema(description = "회원 ID", example = "1")
        long memberId) {
}
