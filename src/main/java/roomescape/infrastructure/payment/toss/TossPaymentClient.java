package roomescape.infrastructure.payment.toss;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.payment.PaymentErrorHandler;
import roomescape.infrastructure.payment.toss.dto.request.TossPaymentRequest;
import roomescape.infrastructure.payment.toss.dto.response.TossPaymentResponse;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final PaymentErrorHandler errorHandler;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
        this.errorHandler = new PaymentErrorHandler();
    }

    @Override
    public TossPaymentResponse requestPayment(final TossPaymentRequest tossPaymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossPaymentRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(TossPaymentResponse.class);
    }
}
