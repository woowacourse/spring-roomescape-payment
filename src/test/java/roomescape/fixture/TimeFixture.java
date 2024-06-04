package roomescape.fixture;

import roomescape.domain.schedule.ReservationTime;
import roomescape.service.schedule.dto.ReservationTimeCreateRequest;

import java.time.LocalTime;

public class TimeFixture {
    public static ReservationTime createTime() {
        return new ReservationTime(LocalTime.now());
    }

    public static ReservationTime createTime(int hour, int minute) {
        return new ReservationTime(LocalTime.of(hour, minute));
    }

    public static ReservationTimeCreateRequest createTimeCreateRequest() {
        return new ReservationTimeCreateRequest(LocalTime.of(10, 0));
    }

    public static ReservationTimeCreateRequest createTimeCreateRequest(LocalTime localTime) {
        return new ReservationTimeCreateRequest(localTime);
    }
}
