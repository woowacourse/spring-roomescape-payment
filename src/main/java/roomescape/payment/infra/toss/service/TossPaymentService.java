package roomescape.payment.infra.toss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import roomescape.logging.aspect.Loggable;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.infra.toss.client.TossPaymentClient;
import roomescape.payment.infra.toss.dto.TossPaymentRequest;
import roomescape.payment.service.PaymentService;

@Service
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final TossPaymentClient tossPaymentClient;

    @Loggable
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Retryable(retryFor = {PaymentTemporaryException.class, ResourceAccessException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 500))
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        TossPaymentRequest tossPaymentRequest = TossPaymentRequest.from(paymentRequest);

        return tossPaymentClient.getPaymentConfirm(tossPaymentRequest)
                .toPaymentResponse();
    }

    @Loggable
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Retryable(retryFor = {PaymentTemporaryException.class, ResourceAccessException.class},
            maxAttempts = 2, backoff = @Backoff(delay = 500))
    public PaymentResponse getPayment(String paymentKey) {
        return tossPaymentClient.getPayment(paymentKey)
                .toPaymentResponse();
    }
}
