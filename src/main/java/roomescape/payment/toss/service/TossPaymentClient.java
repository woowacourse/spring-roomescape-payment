package roomescape.payment.toss.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.payment.exception.PaymentTemporaryException;
import roomescape.payment.toss.dto.TossPaymentResponse;
import roomescape.payment.toss.dto.TossPaymentRequest;

@HttpExchange
public interface TossPaymentClient {

    @PostExchange("/confirm")
    @Retryable(retryFor = {PaymentTemporaryException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    TossPaymentResponse getPaymentConfirm(@RequestBody TossPaymentRequest paymentRequest);
}
