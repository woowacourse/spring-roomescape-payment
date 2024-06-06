package roomescape.service.httpclient;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentRequest;
import roomescape.model.Payment;

public class TossPaymentRestClient extends TossPaymentClient {

    private final RestClient restClient;

    public TossPaymentRestClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    protected Payment request(final PaymentRequest paymentRequest) {
        return restClient.post()
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .toEntity(Payment.class)
                .getBody();
    }
}
