package roomescape.fixture;

import roomescape.domain.schedule.ReservationDate;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.schedule.Schedule;

import java.time.LocalDate;

public class ScheduleFixture {
    public static Schedule createFutureSchedule(ReservationTime time) {
        ReservationDate date = ReservationDate.of(LocalDate.now().plusDays(1));
        return new Schedule(date, time);
    }

    public static Schedule createFutureSchedule(long plusDays, ReservationTime time) {
        ReservationDate date = ReservationDate.of(LocalDate.now().plusDays(plusDays));
        return new Schedule(date, time);
    }
}
