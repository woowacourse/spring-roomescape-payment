package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.exception.RequestPaymentErrorHandler;
import roomescape.payment.domain.PaymentClient;
import roomescape.reservation.presentation.dto.PaymentRequest;
import roomescape.payment.infrastructure.dto.PaymentResponse;

public class TossPaymentClient implements PaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse requestPayment(final PaymentRequest paymentRequest) {
        return restClient.post()
                .uri("/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new RequestPaymentErrorHandler(new ObjectMapper()))
                .body(PaymentResponse.class);
    }
}
