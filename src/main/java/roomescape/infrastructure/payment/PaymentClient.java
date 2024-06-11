package roomescape.infrastructure.payment;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.dto.request.payment.PaymentRequest;
import roomescape.dto.response.payment.PaymentResponse;

@HttpExchange
public interface PaymentClient {
    @PostExchange("/v1/payments/confirm")
    PaymentResponse pay(@RequestBody PaymentRequest paymentRequest);

    @PostExchange("/v1/payments/{paymentKey}/cancel")
    void cancel(@PathVariable String paymentKey, @RequestBody Map<String, String> header);
}
