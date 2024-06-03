package roomescape.fixture;

import java.time.LocalDate;

public class DateFixture {

    public static final LocalDate BEFORE_DATE = LocalDate.now().minusDays(1);
    public static final LocalDate TOMORROW_DATE = LocalDate.now().plusDays(1);
    public static final LocalDate TWO_DAYS_LATER_DATE = LocalDate.now().plusDays(2);
}
