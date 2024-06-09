package roomescape.component;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import roomescape.config.payment.TossPaymentConfigProperties;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;

@Component
public class TossPaymentClient {

    private final String confirmUri;
    private final RestClient restClient;

    public TossPaymentClient(
            TossPaymentConfigProperties properties,
            RestClient.Builder tossRestClientBuilder
    ) {
        this.restClient = tossRestClientBuilder.build();
        this.confirmUri = properties.confirmUri();
    }

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest paymentConfirmRequest) {
        return restClient.post()
                .uri(confirmUri)
                .body(paymentConfirmRequest)
                .retrieve()
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
