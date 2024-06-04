package roomescape.domain.theme;

import java.time.Clock;
import java.time.LocalDate;

public class PopularThemeFilter {

    private static final int START_DATE_OFFSET = 6;
    private static final int END_DATE_OFFSET = 1;
    private static final int LIMIT = 10;

    private final LocalDate endDate;
    private final LocalDate startDate;
    private final int limit;

    public PopularThemeFilter(LocalDate date, Integer days, Integer limit, Clock clock) {
        this.endDate = getOrDefaultEndDate(date, clock);
        this.startDate = getOrDefaultStartDate(days, endDate);
        this.limit = getOrDefaultLimit(limit);
    }

    private LocalDate getOrDefaultEndDate(LocalDate date, Clock clock) {
        if (date == null) {
            return LocalDate.now(clock).minusDays(END_DATE_OFFSET);
        }
        return date;
    }

    private LocalDate getOrDefaultStartDate(Integer days, LocalDate endDate) {
        if (days == null) {
            return endDate.minusDays(START_DATE_OFFSET);
        }
        return endDate.minusDays(days);
    }

    private Integer getOrDefaultLimit(Integer limit) {
        if (limit == null) {
            return LIMIT;
        }
        return limit;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getLimit() {
        return limit;
    }
}
