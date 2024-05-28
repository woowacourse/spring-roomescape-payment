package roomescape.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;

import java.util.Base64;

public class PaymentClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClient.class);
    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:"; // TODO 얘 어캄

    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<Void> postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());

        return restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .toBodilessEntity();
    }
}
