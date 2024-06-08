package roomescape.support.fixture;

import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;

import java.math.BigDecimal;

public class PaymentFixture {

    public static Payment create(Reservation reservation) {
        return new Payment("paymentKey", new BigDecimal("10000"), reservation);
    }
}
