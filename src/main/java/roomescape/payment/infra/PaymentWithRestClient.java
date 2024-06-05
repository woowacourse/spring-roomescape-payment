package roomescape.payment.infra;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.exception.handler.TossPaymentErrorHandler;
import roomescape.payment.application.PaymentClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public class PaymentWithRestClient implements PaymentClient {

    private final RestClient restClient;

    public PaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new TossPaymentErrorHandler())
                .body(PaymentResponse.class);
    }
}
