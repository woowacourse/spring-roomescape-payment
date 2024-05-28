package roomescape.client;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public PaymentResponse pay(PaymentRequest request) {
        PaymentResponse paymentResponse = restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
        return paymentResponse;
    }
}
