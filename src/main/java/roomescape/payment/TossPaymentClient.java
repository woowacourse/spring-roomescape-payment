package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;

import java.util.Base64;

public class TossPaymentClient implements PaymentClient {

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
    public void postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString((secretKey + password).getBytes());

        restClient.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest);
    }
}
