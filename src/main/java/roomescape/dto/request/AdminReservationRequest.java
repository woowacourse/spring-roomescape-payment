package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AdminReservationRequest(
        @Schema(description = "예약 요청 날짜") LocalDate date,
        @Schema(description = "예약 요청 시간") long timeId,
        @Schema(description = "예약 요청 테마") long themeId,
        @Schema(description = "예약 요청 회원") long memberId
) {
}
