package roomescape.reservation.domain.vo;

import roomescape.theme.domain.Theme;

public record ThemeBookingCount(Theme theme, BookedCount bookedCount) {

    public ThemeBookingCount(Theme theme, Long bookedCount) {
        this(theme, BookedCount.from(bookedCount));
    }
}
