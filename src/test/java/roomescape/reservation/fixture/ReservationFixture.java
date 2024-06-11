package roomescape.reservation.fixture;

import static roomescape.theme.fixture.ThemeFixture.THEME_1;
import static roomescape.theme.fixture.ThemeFixture.THEME_2;
import static roomescape.time.fixture.DateTimeFixture.TOMORROW;
import static roomescape.time.fixture.DateTimeFixture.YESTERDAY;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_11_00_ID_2;

import java.math.BigDecimal;
import roomescape.member.fixture.MemberFixture;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.ReservationRequest;

public class ReservationFixture {

    public static final ReservationRequest RESERVATION_REQUEST_1 = new ReservationRequest(
            TOMORROW,
            1L,
            1L,
            1L);


    public static final ReservationRequest PAST_DATE_RESERVATION_REQUEST = new ReservationRequest(
            YESTERDAY,
            1L,
            1L,
            1L);

    public static final MemberReservationAddRequest RESERVATION_ADD_REQUEST_WITH_INVALID_PAYMENTS = new MemberReservationAddRequest(
            TOMORROW,
            1L,
            1L,
            "invalid payment key",
            "invalid order id",
            BigDecimal.valueOf(1000L));

    public static final MemberReservationAddRequest RESERVATION_ADD_REQUEST_WITH_VALID_PAYMENTS = new MemberReservationAddRequest(
            TOMORROW,
            1L,
            1L,
            "valid payment key",
            "valid order id",
            BigDecimal.valueOf(1000L));
    public static final Reservation SAVED_RESERVATION_1 = new Reservation(
            1L,
            MemberFixture.MEMBER_ID_1,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);

    public static final Reservation SAVED_RESERVATION_2 = new Reservation(
            2L,
            MemberFixture.MEMBER_ID_2,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_2);

    public static final Reservation SAVED_RESERVATION_3 = new Reservation(
            3L,
            MemberFixture.MEMBER_ID_3,
            TOMORROW,
            RESERVATION_TIME_11_00_ID_2,
            THEME_1);

    public static final Reservation SECOND_ORDER_RESERVATION = new Reservation(
            4L,
            MemberFixture.MEMBER_ID_2,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);

    public static final Reservation THIRD_ORDER_RESERVATION = new Reservation(
            5L,
            MemberFixture.MEMBER_ID_3,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);

    public static final Reservation MEMBER_ID_1_RESERVATION = new Reservation(
            1L,
            MemberFixture.MEMBER_ID_1,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);

    public static final Reservation RESERVED = new Reservation(
            1L,
            MemberFixture.MEMBER_ID_1,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);

    public static final Reservation WAITING_2 = new Reservation(
            2L,
            MemberFixture.MEMBER_ID_2,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);

    public static final Reservation WAITING_3 = new Reservation(
            3L,
            MemberFixture.MEMBER_ID_3,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1);
}
