package roomescape.payment.infra.toss.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.payment.infra.toss.dto.TossPaymentRequest;
import roomescape.payment.infra.toss.dto.TossPaymentResponse;

@HttpExchange
public interface TossPaymentClient {

    @PostExchange("/confirm")
    TossPaymentResponse getPaymentConfirm(@RequestBody TossPaymentRequest paymentRequest);

    @GetExchange("/{paymentKey}")
    TossPaymentResponse getPayment(@PathVariable String paymentKey);
}
