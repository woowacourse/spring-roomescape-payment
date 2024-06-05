package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.payment.dto.PaymentRequest;

import java.net.SocketTimeoutException;
import java.util.Base64;

public class TossPaymentClient implements PaymentClient{

    @Value("${payments.toss.secret-key}")
    private String secretKey;
    @Value("${payments.toss.password}")
    private String password;

    private final RestClient restClient;

    public TossPaymentClient(final HttpComponentsClientHttpRequestFactory factory) {
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.tosspayments.com")
                .build();
    }

    @Override
    public ResponseEntity<Void> postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString((secretKey+password).getBytes());

        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", secret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(paymentRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
