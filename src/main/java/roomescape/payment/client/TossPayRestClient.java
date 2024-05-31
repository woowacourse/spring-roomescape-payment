package roomescape.payment.client;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;

public class TossPayRestClient {

    private final RestClient restClient;

    public TossPayRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void pay(PaymentRequest paymentRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
