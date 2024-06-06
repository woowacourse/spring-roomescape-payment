package roomescape.infrastructure.payment;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

@HttpExchange
public interface PaymentClient {
    @PostExchange("/v1/payments/confirm")
    PaymentResponse pay(@RequestBody PaymentRequest paymentRequest);
}
