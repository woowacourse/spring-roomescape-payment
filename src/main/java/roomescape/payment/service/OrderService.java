package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Orders;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.OrdersRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentResponse createOrder(PaymentRequest paymentRequest) {
        Orders orders = Orders.pending(paymentRequest.paymentKey(), paymentRequest.orderId());

        Orders save = ordersRepository.save(orders);

        return PaymentResponse.from(save);
    }
}
