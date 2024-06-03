package roomescape.admin.dto;

import roomescape.admin.domain.FilterInfo;

import java.time.LocalDate;

public record ReservationFilterRequest(Long memberId, Long themeId, LocalDate from, LocalDate to) {

    public FilterInfo toFilterInfo() {
        return new FilterInfo(memberId, themeId, from, to);
    }
}
