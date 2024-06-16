package roomescape.support.mock;

import java.time.LocalDate;
import roomescape.application.policy.RankingPolicy;

public class FakeRankingPolicy implements RankingPolicy {
    private final String startDate = LocalDate.now().plusDays(1).toString();
    private final String endDate = LocalDate.now().plusDays(4).toString();

    @Override
    public LocalDate getStartDateAsString() {
        return LocalDate.parse(startDate);
    }

    @Override
    public LocalDate getEndDateAsString() {
        return LocalDate.parse(endDate);
    }

    @Override
    public int exposureSize() {
        return 3;
    }
}
