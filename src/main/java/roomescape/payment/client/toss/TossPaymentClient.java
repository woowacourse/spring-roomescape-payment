package roomescape.payment.client.toss;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.PaymentInfoFromClient;

@Component
public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentInfoFromClient confirm(ConfirmPaymentRequest confirmPaymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .body(confirmPaymentRequest)
                .retrieve()
                .toEntity(PaymentInfoFromClient.class)
                .getBody();
    }
}
