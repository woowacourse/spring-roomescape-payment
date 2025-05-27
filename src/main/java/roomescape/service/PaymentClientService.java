package roomescape.service;

import org.springframework.web.client.RestClient;
import roomescape.domain.PaymentInfo;
import roomescape.dto.PaymentRequest;

public class PaymentClientService {
    private final RestClient restClient;

    public PaymentClientService(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentInfo postPaymentInfo(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(paymentRequest)
                .retrieve()
                .body(PaymentInfo.class);
    }
}
