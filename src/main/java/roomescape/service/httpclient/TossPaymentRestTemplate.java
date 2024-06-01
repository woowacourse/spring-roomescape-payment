package roomescape.service.httpclient;


import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import roomescape.controller.request.PaymentRequest;

@Component
public class TossPaymentRestTemplate extends TossPaymentClient{

    private final RestTemplate restTemplate;

    public TossPaymentRestTemplate(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected void request(final PaymentRequest paymentRequest) {
        restTemplate.postForLocation(CONFIRM_URI, paymentRequest);
    }
}
