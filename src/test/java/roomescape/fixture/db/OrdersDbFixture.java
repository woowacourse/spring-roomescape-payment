package roomescape.fixture.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.payment.domain.Orders;
import roomescape.payment.repository.OrdersRepository;

@RequiredArgsConstructor
@Component
public class OrdersDbFixture {

    private final OrdersRepository ordersRepository;

    public Orders create() {
        Orders orders = new Orders("paymentKey", "orderId");
        return ordersRepository.save(orders);
    }
}
