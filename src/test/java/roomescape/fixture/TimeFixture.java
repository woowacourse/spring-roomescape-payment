package roomescape.fixture;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import roomescape.domain.reservationdetail.ReservationTime;

public class TimeFixture {
    public static List<ReservationTime> createTimes(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createTime(LocalTime.now().plusHours(i)))
                .toList();
    }

    public static ReservationTime createTime(LocalTime startAt) {
        return new ReservationTime(startAt);
    }

    public static ReservationTime createTimeAtExact(int hour) {
        return new ReservationTime(LocalTime.of(hour, 0));
    }
}
