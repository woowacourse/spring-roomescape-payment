package roomescape.fixture;

import roomescape.reservation.domain.Payment;

public class PaymentFixture {
    public static Payment getPayment() {
        return new Payment(1L,
                "paymentKey",
                "orderId",
                "orderName",
                "method",
                1000L,
                "status",
                "requestedAt",
                "approvedAt");
    }

    public static Payment getWrongPayment() {
        return new Payment(1L,
                "paymentKey",
                "orderId",
                "orderName",
                "method",
                0L,
                "status",
                "requestedAt",
                "approvedAt");
    }
}
