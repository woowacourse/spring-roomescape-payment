package roomescape.payment.toss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.toss.dto.TossPaymentRequest;
import roomescape.payment.toss.dto.TossPaymentResponse;

@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossPaymentClient tossPaymentClient;

    @Retryable(retryFor = {PaymentTemporaryException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public TossPaymentResponse confirmPayment(TossPaymentRequest paymentRequest) {
        return tossPaymentClient.getPaymentConfirm(paymentRequest);
    }

    @Retryable(retryFor = {PaymentTemporaryException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public TossPaymentResponse getPayment(TossPaymentRequest paymentRequest) {
        return tossPaymentClient.getPaymentConfirm(paymentRequest);
    }
}
