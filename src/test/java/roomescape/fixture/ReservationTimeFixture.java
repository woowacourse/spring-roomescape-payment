package roomescape.fixture;

import java.time.LocalTime;
import roomescape.domain.schedule.ReservationTime;

public class ReservationTimeFixture {

    public static ReservationTime create10AM() {
        return new ReservationTime(LocalTime.of(10, 0));
    }
}
