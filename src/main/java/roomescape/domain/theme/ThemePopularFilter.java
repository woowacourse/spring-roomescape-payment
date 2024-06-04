package roomescape.domain.theme;

import java.time.LocalDate;
import java.util.Objects;

public class ThemePopularFilter {

    private static final int POPULARITY_AGGREGATION_PERIOD = 7;
    private static final int POPULARITY_AGGREGATION_LIMIT = 10;

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int limit;

    private ThemePopularFilter(final LocalDate startDate, final LocalDate endDate, final int limit) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.limit = limit;
    }

    public static ThemePopularFilter getThemePopularFilter(final LocalDate nowDate) {
        final LocalDate startDate = nowDate.minusDays(POPULARITY_AGGREGATION_PERIOD);
        return new ThemePopularFilter(startDate, nowDate, POPULARITY_AGGREGATION_LIMIT);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ThemePopularFilter other
                && Objects.equals(startDate, other.startDate)
                && Objects.equals(endDate, other.endDate)
                && limit == other.limit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, limit);
    }
}
