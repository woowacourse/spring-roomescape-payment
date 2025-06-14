package fixture;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import roomescape.reservation.entity.ReservationTime;

public class ReservationTimeFixture {

    private static LocalTime time = LocalTime.MIN;

    public static ReservationTime create(LocalTime startAt) {
        return new ReservationTime(startAt);
    }

    public static ReservationTime createDefault() {
        return create(getAndIncrementTime());
    }

    public static List<ReservationTime> createDefaultList(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> createDefault())
                .collect(Collectors.toList());
    }

    private static LocalTime getAndIncrementTime() {
        LocalTime currentTime = time;
        time = time.plusMinutes(1);
        return currentTime;
    }
}
