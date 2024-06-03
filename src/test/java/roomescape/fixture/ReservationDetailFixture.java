package roomescape.fixture;

import roomescape.domain.reservationdetail.ReservationDetail;

public class ReservationDetailFixture {

    public static ReservationDetail create() {
        return new ReservationDetail(
            ScheduleFixture.create(),
            ThemeFixture.create()
        );
    }
}
