package roomescape.component;

import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import roomescape.config.properties.TossPaymentConfigProperties;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;

@Component
public class TossPaymentClient {

    private final TossPaymentConfigProperties properties;
    private final RestClient restClient;

    public TossPaymentClient(
            TossPaymentConfigProperties properties,
            ResponseErrorHandler errorHandler,
            RestClient.Builder tossRestClientBuilder
    ) {
        this.properties = properties;
        this.restClient = tossRestClientBuilder.defaultStatusHandler(errorHandler).build();
    }

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest paymentConfirmRequest) {
        return restClient.post()
                .uri(properties.confirmUri())
                .body(paymentConfirmRequest)
                .retrieve()
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
