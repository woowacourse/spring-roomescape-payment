package roomescape.infrastructure.payment;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.request.PaymentRequest;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final PaymentResponseErrorHandler errorHandler;

    public PaymentClient(final RestClient restClient, final PaymentResponseErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public PaymentDto approve(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentDto.class);
    }
}
