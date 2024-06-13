package roomescape.reservation.fixture;

import static roomescape.theme.fixture.ThemeFixture.THEME_1;
import static roomescape.theme.fixture.ThemeFixture.THEME_2;
import static roomescape.time.fixture.DateTimeFixture.TOMORROW;
import static roomescape.time.fixture.DateTimeFixture.YESTERDAY;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_11_00_ID_2;

import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.member.fixture.MemberFixture;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Status;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;

public class ReservationFixture {

    public static final MemberReservationWithPaymentAddRequest RESERVATION_WITH_PAYMENT_REQUEST_1 = new MemberReservationWithPaymentAddRequest(
            TOMORROW,
            1L,
            1L,
            "paymentKey",
            "orderId",
            1000L);

    public static final MemberReservationWithPaymentAddRequest PAST_RESERVATION_WITH_PAYMENT_REQUEST_1 = new MemberReservationWithPaymentAddRequest(
            YESTERDAY,
            1L,
            1L,
            "paymentKey",
            "orderId",
            1000L);

    public static final MemberReservationAddRequest RESERVATION_REQUEST_1 = new MemberReservationAddRequest(
            TOMORROW,
            1L,
            1L);

    public static final MemberReservationAddRequest PAST_DATE_RESERVATION_REQUEST = new MemberReservationAddRequest(
            YESTERDAY,
            1L,
            1L);

    public static final Reservation SAVED_RESERVATION_1 = new Reservation(
            1L,
            MemberFixture.MEMBER_ID_1,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1,
            Status.RESERVED,
            LocalDateTime.of(TOMORROW, LocalTime.parse("00:00"))
    );

    public static final Reservation SAVED_RESERVATION_2 = new Reservation(
            2L,
            MemberFixture.MEMBER_ID_2,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_2,
            Status.RESERVED,
            LocalDateTime.of(TOMORROW, LocalTime.parse("00:01"))
    );

    public static final Reservation SAVED_RESERVATION_3 = new Reservation(
            3L,
            MemberFixture.MEMBER_ID_3,
            TOMORROW,
            RESERVATION_TIME_11_00_ID_2,
            THEME_1,
            Status.RESERVED,
            LocalDateTime.of(TOMORROW, LocalTime.parse("00:02"))
    );

    public static final Reservation SAVED_WAITING_RESERVATION_1 = new Reservation(
            4L,
            MemberFixture.MEMBER_ID_2,
            TOMORROW,
            RESERVATION_TIME_10_00_ID_1,
            THEME_1,
            Status.WAITING,
            LocalDateTime.of(TOMORROW, LocalTime.parse("00:02"))
    );
}
