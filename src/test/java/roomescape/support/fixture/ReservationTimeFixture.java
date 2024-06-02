package roomescape.support.fixture;

import roomescape.domain.reservationtime.ReservationTime;

import java.time.LocalTime;

public class ReservationTimeFixture {

    public static ReservationTime ten() {
        return create("10:00");
    }

    public static ReservationTime create(String time) {
        return new ReservationTime(LocalTime.parse(time));
    }
}
