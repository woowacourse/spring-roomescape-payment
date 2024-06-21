package roomescape.fixture;

import java.util.List;

import roomescape.domain.payment.Payment;

public class PaymentFixture {

    public static final List<Payment> PAYMENTS = List.of(
            new Payment(1L, "payment-key-1", "order-id-1", 1000L),
            new Payment(2L, "payment-key-2", "order-id-2", 2000L),
            new Payment(3L, "payment-key-3", "order-id-3", 3000L),
            new Payment(4L, "payment-key-4", "order-id-4", 4000L)
    );

    public static Payment paymentFixture(long id) {
        assert id <= PAYMENTS.size();
        return PAYMENTS.get((int) (id - 1));
    }
}
