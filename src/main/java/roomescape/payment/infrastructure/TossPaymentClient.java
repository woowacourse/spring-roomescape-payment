package roomescape.payment.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.exception.TossPaymentErrorHandler;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final TossPaymentErrorHandler errorHandler;

    public TossPaymentClient(final RestClient restClient) {
        this.restClient = restClient;
        this.errorHandler = new TossPaymentErrorHandler();
    }

    @Override
    public PaymentResponse requestPayment(final PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(errorHandler)
                .body(PaymentResponse.class);
    }
}
