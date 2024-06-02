package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

public class TossPaymentClient {

    private final RestClient restClient;

    @Value("${payment.api.confirm}")
    private String confirmApi;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentConfirmResponse confirmPayments(PaymentConfirmRequest request) {
        return restClient.post()
                .uri(confirmApi)
                .body(request)
                .retrieve()
                .toEntity(PaymentConfirmResponse.class)
                .getBody();
    }
}
