package roomescape.fixture;

import roomescape.payment.model.Payment;
import roomescape.reservation.model.Reservation;

public class PaymentFixture {

    public static Payment getOne() {
        return new Payment(ReservationFixture.getOneWithId(1L), "abcde", "qwer", 1000L);
    }

    public static Payment getOneWithReservation(Reservation reservation) {
        return new Payment(reservation, "abcde", "qwer", 1000L);
    }
}
