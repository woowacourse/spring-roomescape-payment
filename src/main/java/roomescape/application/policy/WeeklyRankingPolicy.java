package roomescape.application.policy;

import java.time.LocalDate;

public class WeeklyRankingPolicy implements RankingPolicy {

    private static final int DAYS_BEFORE_START = 8;
    private static final int DAYS_BEFORE_END = 1;
    private static final int EXPOSURE_SIZE = 10;

    @Override
    public LocalDate getStartDateAsString() {
        return LocalDate.now()
                .minusDays(DAYS_BEFORE_START);
    }

    @Override
    public LocalDate getEndDateAsString() {
        return LocalDate.now()
                .minusDays(DAYS_BEFORE_END);
    }

    @Override
    public int exposureSize() {
        return EXPOSURE_SIZE;
    }
}
