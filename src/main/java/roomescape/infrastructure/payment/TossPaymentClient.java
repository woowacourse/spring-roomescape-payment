package roomescape.infrastructure.payment;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClient;
import roomescape.service.dto.request.PaymentCancelRequest;
import roomescape.service.dto.request.PaymentConfirmRequest;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void pay(PaymentConfirmRequest paymentConfirmRequest) {
        restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentConfirmRequest)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void cancel(PaymentCancelRequest paymentCancelRequest) {
        restClient.post()
                .uri("/{paymentKey}/cancel", paymentCancelRequest.paymentKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCancelRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
