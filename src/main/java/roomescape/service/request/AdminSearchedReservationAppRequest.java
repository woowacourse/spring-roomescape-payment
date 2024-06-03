package roomescape.service.request;

import java.time.LocalDate;
import roomescape.web.controller.request.SearchCondition;

public record AdminSearchedReservationAppRequest(Long memberId, Long themeId, LocalDate dateFrom, LocalDate dateTo) {

    public static AdminSearchedReservationAppRequest from(SearchCondition searchCondition) {
        return new AdminSearchedReservationAppRequest(
                searchCondition.memberId(),
                searchCondition.themeId(),
                searchCondition.dateFrom(),
                searchCondition.dateTo()
        );
    }
}
