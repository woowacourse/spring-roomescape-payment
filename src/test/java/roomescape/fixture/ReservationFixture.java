package roomescape.fixture;

import java.time.LocalDate;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

public class ReservationFixture {
    public static ReservationInfo getNextDayReservation(ReservationTime time, Theme theme) {
        return new ReservationInfo(LocalDate.now().plusDays(1), time, theme);
    }

    public static ReservationInfo getNextMonthReservation(ReservationTime time, Theme theme) {
        return new ReservationInfo(LocalDate.now().plusMonths(1), time, theme);
    }
}
