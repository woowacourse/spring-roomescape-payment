package roomescape.payment.infra.toss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import roomescape.payment.domain.Orders;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.infra.toss.client.TossPaymentClient;
import roomescape.payment.infra.toss.dto.TossPaymentRequest;
import roomescape.payment.repository.OrdersRepository;
import roomescape.payment.service.PaymentService;

@Service
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final OrdersRepository ordersRepository;
    private final TossPaymentClient tossPaymentClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentResponse createOrder(PaymentRequest paymentRequest) {
        Orders orders = new Orders(paymentRequest.paymentKey(), paymentRequest.orderId());

        Orders save = ordersRepository.save(orders);

        return PaymentResponse.from(save);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Retryable(retryFor = {PaymentTemporaryException.class, ResourceAccessException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 500))
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        TossPaymentRequest tossPaymentRequest = TossPaymentRequest.from(paymentRequest);

        return tossPaymentClient.getPaymentConfirm(tossPaymentRequest)
                .toPaymentResponse();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Retryable(retryFor = {PaymentTemporaryException.class, ResourceAccessException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 500))
    public PaymentResponse getPayment(String paymentKey) {
        return tossPaymentClient.getPayment(paymentKey)
                .toPaymentResponse();
    }
}
