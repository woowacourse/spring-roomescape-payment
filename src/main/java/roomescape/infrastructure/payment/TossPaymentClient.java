package roomescape.infrastructure.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;

@Component
@Profile("!local")
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment approve(PaymentRequest request) {
        return restClient.post()
            .uri("/v1/payments/confirm")
            .contentType(APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(Payment.class);
    }
}
