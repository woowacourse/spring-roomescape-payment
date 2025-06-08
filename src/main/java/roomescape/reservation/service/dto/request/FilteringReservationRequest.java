package roomescape.reservation.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "FilteringReservationRequest(예약 필터링 요청 DTO)")
public record FilteringReservationRequest(
        Long themeId,
        Long memberId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
