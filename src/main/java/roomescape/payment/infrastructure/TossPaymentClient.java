package roomescape.payment.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.request.TossPaymentRequest;
import roomescape.payment.dto.response.TossPaymentResponse;
import roomescape.payment.exception.TossPaymentErrorHandler;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final TossPaymentErrorHandler errorHandler;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
        this.errorHandler = new TossPaymentErrorHandler();
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
