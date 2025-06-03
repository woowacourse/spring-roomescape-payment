package roomescape.helper;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationSlot;
import roomescape.reservation.entity.ReservationTime;
import roomescape.theme.entity.Theme;

public class TestFixture {
    public static final ReservationTime RESERVATION_TIME = new ReservationTime(LocalTime.of(10, 0));
    public static final Theme THEME = new Theme("테마", "설명", "썸네일");
    public static final Member MEMBER = new Member("김유저", "user@email.com", "password", RoleType.USER);
    public static final ReservationSlot RESERVATION_SLOT = new ReservationSlot(LocalDate.now().plusDays(1), RESERVATION_TIME, THEME);
    public static final Reservation RESERVATION = new Reservation(RESERVATION_SLOT, MEMBER);
    public static final Payment PAYMENT = new Payment("paymentKey", "orderId", 1000L, "NORMAL", RESERVATION);
}
