package roomescape.payment.fixture;

import static roomescape.theme.fixture.ThemeFixture.THEME_1;
import static roomescape.time.fixture.DateTimeFixture.TOMORROW;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1;

import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.member.fixture.MemberFixture;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Status;

public class PaymentFixture {

    public static final Payment PAYMENT_1 = new Payment(1L,
            new Reservation(
                    1L,
                    MemberFixture.MEMBER_ID_1,
                    TOMORROW,
                    RESERVATION_TIME_10_00_ID_1,
                    THEME_1,
                    Status.RESERVED,
                    LocalDateTime.of(TOMORROW, LocalTime.parse("00:00"))
            ),
            "test_payment_key",
            "test_order_id",
            20000L);
}
