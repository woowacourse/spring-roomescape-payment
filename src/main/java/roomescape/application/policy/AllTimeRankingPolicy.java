package roomescape.application.policy;

import java.time.LocalDate;

public class AllTimeRankingPolicy implements RankingPolicy {

    private static final int YEARS_TO_ADD = 1;
    private static final int LIMIT = 10;

    @Override
    public LocalDate getStartDateAsString() {
        return LocalDate.MIN;
    }

    @Override
    public LocalDate getEndDateAsString() {
        return LocalDate.now().plusYears(YEARS_TO_ADD);
    }

    @Override
    public int exposureSize() {
        return LIMIT;
    }
}
