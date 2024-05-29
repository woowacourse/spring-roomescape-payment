package roomescape.payment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Payment confirm(ConfirmPaymentRequest confirmPaymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .body(confirmPaymentRequest)
                .retrieve()
                .toEntity(Payment.class)
                .getBody();
    }
}
