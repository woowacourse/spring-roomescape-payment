package roomescape.fixture;

import java.math.BigDecimal;
import roomescape.domain.Payment;

public class PaymentFixture {

    public static final Payment DEFAULT_PAYMENT = new Payment(
            1L,
            "paymentKey",
            "WTESTOrderId",
            BigDecimal.valueOf(1000),
            ReservationFixture.DEFAULT_RESERVATION
    );
}
