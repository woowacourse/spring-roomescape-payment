package roomescape.service.httpclient;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.controller.request.PaymentRequest;
import roomescape.model.Payment;

@Component
public class TossPaymentRestTemplate extends TossPaymentClient {

    private final RestTemplate restTemplate;

    public TossPaymentRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected Payment request(final PaymentRequest paymentRequest) {
        return restTemplate.postForObject(CONFIRM_URI, paymentRequest, Payment.class);
    }
}
