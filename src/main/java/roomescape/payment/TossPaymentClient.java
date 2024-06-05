package roomescape.payment;

import org.springframework.web.client.RestClient;
import roomescape.global.config.TossPaymentProperties;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public class TossPaymentClient {

    private final RestClient restClient;
    private final TossPaymentProperties tossPaymentProperties;

    public TossPaymentClient(RestClient restClient, TossPaymentProperties tossPaymentProperties) {
        this.restClient = restClient;
        this.tossPaymentProperties = tossPaymentProperties;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        return restClient.post()
                .uri(tossPaymentProperties.api().confirm())
                .body(request)
                .retrieve()
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
