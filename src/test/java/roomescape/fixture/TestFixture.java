package roomescape.fixture;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.ReservationTheme;
import roomescape.time.domain.ReservationTime;
import roomescape.waiting.domain.ReservationWaiting;

public class TestFixture {

    public static final Member USER_MEMBER = new Member(
            "user@email.com",
            "password",
            "테스트 유저",
            MemberRole.USER);

    public static final Member ADMIN_MEMBER = new Member(
            "admin@email.com",
            "password",
            "테스트 어드민",
            MemberRole.ADMIN);

    public static final ReservationTheme THEME = new ReservationTheme(
            1L,
            "테마 이름",
            "테마 설명",
            "테마 이미지 URL");

    public static final ReservationTime TIME = new ReservationTime(
            1L,
            LocalTime.of(10, 0));

    public static final Payment PAYMENT = new Payment(
            "testOrderId",
            "testPaymentKey",
            1000L,
            PaymentType.NORMAL);

    public static final Reservation RESERVATION = new Reservation(
            1L,
            USER_MEMBER,
            LocalDate.now().plusDays(1),
            TIME,
            THEME
    );
    public static final ReservationWaiting RESERVATION_WAITING = new ReservationWaiting(
            1L,
            USER_MEMBER,
            LocalDate.now().plusDays(1),
            TIME,
            THEME
    );
}
