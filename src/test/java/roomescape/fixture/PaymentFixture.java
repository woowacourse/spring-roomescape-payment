package roomescape.fixture;

import roomescape.reservation.domain.Payment;

public class PaymentFixture {
    public static Payment getPaymentWithId() {
        return new Payment(1L,
                "payment_key",
                "order_id",
                "order_name",
                "method",
                1000L,
                "status",
                "requested_at",
                "approved_at");
    }

    public static Payment getWrongPaymentWithId() {
        return new Payment(1L,
                "payment_key",
                "order_id",
                "order_name",
                "method",
                0L,
                "status",
                "requested_at",
                "approved_at");
    }

    public static Payment getPaymentWithoutId() {
        return new Payment(
                "test_payment_key",
                "test_order_id",
                "test_order_name",
                "test_method",
                1000L,
                "test_status",
                "test_requested_at",
                "test_approved_at");
    }
}
