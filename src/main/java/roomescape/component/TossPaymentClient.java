package roomescape.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;

@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final ResponseErrorHandler errorHandler;

    @Value("${payment.toss.confirm-url}")
    private String confirmUri;

    public TossPaymentClient(final RestClient restClient, final ResponseErrorHandler errorHandler) {
        this.restClient = restClient;
        this.errorHandler = errorHandler;
    }

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest paymentConfirmRequest) {
        return restClient.post()
                .uri(confirmUri)
                .body(paymentConfirmRequest)
                .retrieve()
                .onStatus(errorHandler)
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
