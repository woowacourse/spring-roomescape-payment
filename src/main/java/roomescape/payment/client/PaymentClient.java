package roomescape.payment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public Payment confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .body(confirmPaymentRequest)
                .retrieve()
                .toEntity(Payment.class)
                .getBody();
    }
}
