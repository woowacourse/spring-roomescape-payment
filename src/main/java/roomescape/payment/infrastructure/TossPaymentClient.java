package roomescape.payment.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.infrastructure.dto.request.TossPaymentRequest;
import roomescape.payment.infrastructure.dto.response.TossPaymentResponse;
import roomescape.payment.exception.PaymentErrorHandler;

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
