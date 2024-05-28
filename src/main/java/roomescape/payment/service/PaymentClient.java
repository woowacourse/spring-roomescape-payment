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

    public ResponseEntity<PaymentResponse> confirm(PaymentRequest paymentRequest) {
        try {
            return restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header("Authorization", "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6")
                    .body(paymentRequest)
                    .retrieve().toEntity(PaymentResponse.class);
        } catch (HttpClientErrorException e) {
            PaymentErrorResponse paymentResponse = e.getResponseBodyAs(PaymentErrorResponse.class);
            throw new PaymentException(paymentResponse);
        }

    }
}
