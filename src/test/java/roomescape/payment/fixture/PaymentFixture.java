package roomescape.payment.fixture;

import roomescape.payment.domain.Payment;

public class PaymentFixture {

    public static final Payment PAYMENT_1 = new Payment(1L,
            1L,
            "test_payment_key",
            "test_order_id",
            20000L);
}
