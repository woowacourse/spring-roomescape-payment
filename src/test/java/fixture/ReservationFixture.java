package fixture;

import java.time.LocalDate;
import roomescape.member.entity.Member;
import roomescape.payment.entity.Payment;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationTime;
import roomescape.theme.entity.Theme;

public class ReservationFixture {

    public static Reservation create(
            LocalDate date,
            ReservationTime time,
            Theme theme,
            Member member,
            Payment payment
    ) {
        return new Reservation(date, time, theme, member, payment);
    }
}
