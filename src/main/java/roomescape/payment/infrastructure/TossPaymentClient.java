package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.exception.TossPaymentErrorHandler;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;
    private final TossPaymentErrorHandler errorHandler;

    public TossPaymentClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.errorHandler = new TossPaymentErrorHandler(objectMapper);
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
