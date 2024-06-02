package roomescape.fixture;

import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.Schedule;
import roomescape.domain.theme.Theme;

public class ReservationDetailFixture {

    public static ReservationDetail create(Theme theme, Schedule schedule) {
        return new ReservationDetail(schedule, theme);
    }
}
