package roomescape.fixture;

import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

public class ReservationFixture {

    public static final Reservation RESERVATION_WITH_ID = new Reservation(1L, MemberFixture.MEMBER_BROWN,
            DateFixture.TOMORROW_DATE, TimeFixture.TIME_WITH_ID, ThemeFixture.THEME_WITH_ID,
            ReservationStatus.RESERVED);
}
