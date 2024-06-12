package roomescape.infrastructure;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.PaymentInfo;

@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentClient(final RestClient restClient, final TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public void confirmPayment(PaymentInfo paymentInfo) {
        restClient.post()
                .uri(tossPaymentProperties.url().confirm())
                .body(paymentInfo)
                .retrieve()
                .toBodilessEntity();
    }
}
