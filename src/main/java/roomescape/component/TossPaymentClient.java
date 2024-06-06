package roomescape.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import roomescape.dto.payment.PaymentConfirmRequest;

@Component
public class TossPaymentClient {

    private final RestClient.Builder restClient;
    private final ResponseErrorHandler errorHandler;

    @Value("${payment.toss.confirm-uri}")
    private String confirmUri;

    public TossPaymentClient(RestClient.Builder restClient, ResponseErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public void confirm(final PaymentConfirmRequest paymentConfirmRequest) {
        restClient.build()
                .post()
                .uri(confirmUri)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(errorHandler);
    }
}
