package roomescape.payment;

import org.springframework.web.client.RestClient;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

public class PaymentRestClient {

    private final RestClient restClient;

    public PaymentRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentResponse requestPaymentApprove(final TossPaymentRequest request) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(request)
                .retrieve()
                .body(TossPaymentResponse.class);
    }
}
