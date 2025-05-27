package roomescape.payment.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.dto.PaymentRequest;

@HttpExchange
public interface PaymentClient {

    @PostExchange("/confirm")
    PaymentResponse getPaymentConfirm(@RequestBody PaymentRequest paymentRequest);
}
