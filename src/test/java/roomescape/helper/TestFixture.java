package roomescape.helper;

import roomescape.reservation.entity.Payment;

public class TestFixture {
    public static final Payment PAYMENT = new Payment("paymentKey", "orderId", 1000L, "NORMAL");
}
