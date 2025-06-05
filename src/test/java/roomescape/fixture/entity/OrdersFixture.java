package roomescape.fixture.entity;

import roomescape.payment.domain.Orders;

public class OrdersFixture {

    public static Orders create() {
        return new Orders("paymentKey", "orderId");
    }
}
