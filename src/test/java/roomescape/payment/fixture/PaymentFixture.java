package roomescape.payment.fixture;

import static roomescape.reservation.fixture.ReservationFixture.SAVED_RESERVATION_1;
import static roomescape.time.fixture.DateTimeFixture.TOMORROW;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import roomescape.payment.domain.Payment;

public class PaymentFixture {

    public static final Payment PAYMENT = new Payment(
            1L, SAVED_RESERVATION_1.getId(), "paymentKey", "orderId", "orderName",
            BigDecimal.valueOf(1000), LocalDateTime.of(TOMORROW, RESERVATION_TIME_10_00_ID_1.getStartAt()));
}
