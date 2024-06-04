package roomescape.fixture;

import roomescape.domain.schedule.Schedule;

public class ScheduleFixture {

    public static Schedule create() {
        return new Schedule(
            ReservationDateFixture.create(),
            ReservationTimeFixture.create10AM()
        );
    }
}
