package roomescape.order;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.exception.custom.reason.order.OrderNotFoundException;

@Component
@AllArgsConstructor
public class OrderReader {

    private final OrderRepository orderRepository;

    public Order getById(final String id) {
        return orderRepository.findById(id)
                .orElseThrow(OrderNotFoundException::new);
    }
}
