package roomescape.service.httpclient;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentRequest;

public class TossPaymentRestClient extends TossPaymentClient {

    public TossPaymentRestClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    private final RestClient restClient;

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