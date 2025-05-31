package roomescape.payment.toss.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.payment.toss.dto.TossPaymentResponse;
import roomescape.payment.toss.dto.TossPaymentRequest;

@HttpExchange
public interface TossPaymentClient {

    @PostExchange("/confirm")
    TossPaymentResponse getPaymentConfirm(@RequestBody TossPaymentRequest paymentRequest);
}
