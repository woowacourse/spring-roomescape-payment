package roomescape.payment.client;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.exception.TossPayErrorHandler;
import roomescape.payment.dto.PaymentRequest;

public class TossPayRestClient {

    private final RestClient restClient;
    private final TossPayErrorHandler errorHandler;

    public TossPayRestClient(RestClient restClient, TossPayErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public void pay(PaymentRequest paymentRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(errorHandler)
                .toBodilessEntity();
    }
}
