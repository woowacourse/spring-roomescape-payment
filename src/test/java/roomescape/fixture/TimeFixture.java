package roomescape.fixture;

import java.util.List;

import roomescape.domain.reservation.ReservationTime;

public class TimeFixture {

    public static final List<ReservationTime> TIMES = List.of(
            new ReservationTime(1L, "10:00"),
            new ReservationTime(2L, "11:00"),
            new ReservationTime(3L, "12:00"),
            new ReservationTime(4L, "13:00")
    );

    public static ReservationTime timeFixture(long id) {
        assert id <= TIMES.size();
        return TIMES.get((int) (id - 1));
    }
}
