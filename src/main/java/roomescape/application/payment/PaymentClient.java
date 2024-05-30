package roomescape.application.payment;

import org.springframework.web.client.RestClient;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;

public class PaymentClient {
    private final RestClient client;

    public PaymentClient(RestClient client) {
        this.client = client;
    }

    public Payment requestPurchase(PaymentRequest request) {
        return client.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(Payment.class);
    }
}
