package roomescape.service.httpclient;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentRequest;

public class TossPaymentRestClient extends TossPaymentClient {

    private final RestClient restClient;

    public TossPaymentRestClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    protected void request(final PaymentRequest paymentRequest) {
        restClient.post()
                .uri(CONFIRM_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
