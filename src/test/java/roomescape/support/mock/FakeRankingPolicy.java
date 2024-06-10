package roomescape.support.mock;

import java.time.LocalDate;
import roomescape.application.policy.RankingPolicy;

public class FakeRankingPolicy implements RankingPolicy {
    @Override
    public LocalDate getStartDateAsString() {
        return LocalDate.parse("2024-06-01");
    }

    @Override
    public LocalDate getEndDateAsString() {
        return LocalDate.parse("2024-06-03");
    }

    @Override
    public int exposureSize() {
        return 3;
    }
}
