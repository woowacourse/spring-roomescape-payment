package roomescape.payment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentErrorResponse;
import roomescape.payment.PaymentRequest;
import roomescape.payment.PaymentResponse;
import roomescape.payment.exception.PaymentException;

@Component
public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<PaymentResponse> confirm(PaymentRequest paymentRequest,String encodeKey) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", encodeKey)
                    .body(paymentRequest)
                    .retrieve().toEntity(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse paymentResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(paymentResponse);
        }

    }
}
