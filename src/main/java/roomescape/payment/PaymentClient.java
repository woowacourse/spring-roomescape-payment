package roomescape.payment;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.PaymentException;

import java.util.Base64;

// TODO: 인터페이스로 분리
public class PaymentClient {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";

    private final RestClient restClient;

    public PaymentClient(final HttpComponentsClientHttpRequestFactory factory) {
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.tosspayments.com")
                .build();
    }

    public ResponseEntity<Void> postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());

        return restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new PaymentException(response.getStatusText());
                }))
                .toBodilessEntity();
    }
}
