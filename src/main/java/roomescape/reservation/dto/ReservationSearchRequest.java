package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import roomescape.reservation.domain.ReservationSearch;

public record ReservationSearchRequest(
        @Schema(description = "예약 테마 id", example = "1")
        Long themeId,
        @Schema(description = "예약자 id", example = "1")
        Long memberId,
        @Schema(description = "시작 날짜", example = "2024-05-01")
        LocalDate startDate,
        @Schema(description = "끝 날짜", example = "2024-05-30")
        LocalDate endDate) {
    public ReservationSearch createReservationSearch() {
        return new ReservationSearch(themeId, memberId, startDate, endDate);
    }
}
