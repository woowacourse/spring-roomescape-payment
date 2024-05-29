package roomescape.service.request;

import java.time.LocalDate;
import roomescape.web.controller.request.SearchCondition;

public record AdminSearchedReservationDto(Long memberId, Long themeId, LocalDate dateFrom, LocalDate dateTo) {

    public static AdminSearchedReservationDto from(SearchCondition searchCondition) {
        return new AdminSearchedReservationDto(
                searchCondition.memberId(),
                searchCondition.themeId(),
                searchCondition.dateFrom(),
                searchCondition.dateTo()
        );
    }
}
