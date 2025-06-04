package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.domain.Orders;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.repository.OrdersRepository;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final OrdersRepository ordersRepository;
    private final TossPaymentClient tossPaymentClient;

    @Transactional
    public TossPaymentResponse createOrder(TossPaymentRequest paymentRequest) {
        Orders orders = new Orders(paymentRequest.paymentKey(), paymentRequest.orderId());

        Orders save = ordersRepository.save(orders);
        ordersRepository.flush();

        return TossPaymentResponse.from(save);
    }

    @Retryable(retryFor = {PaymentTemporaryException.class, ResourceAccessException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 500))
    public TossPaymentResponse confirmPayment(TossPaymentRequest paymentRequest) {
        return tossPaymentClient.getPaymentConfirm(paymentRequest);
    }

    @Retryable(retryFor = {PaymentTemporaryException.class, ResourceAccessException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 500))
    public TossPaymentResponse getPayment(String paymentKey) {
        return tossPaymentClient.getPayment(paymentKey);
    }
}
