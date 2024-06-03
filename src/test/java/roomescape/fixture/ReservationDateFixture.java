package roomescape.fixture;

import java.time.LocalDate;
import roomescape.domain.schedule.ReservationDate;

public class ReservationDateFixture {

    public static ReservationDate create() {
        return ReservationDate.of(LocalDate.now().plusDays(1));
    }

    public static ReservationDate createOneDayAgo() {
        return ReservationDate.of(LocalDate.now().minusDays(1));
    }
}
