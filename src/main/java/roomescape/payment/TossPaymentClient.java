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
    @Value("${payments.toss.host-name}")
    private String hostName;
    @Value("${payments.toss.payment-api}")
    private String paymentApi;

    private final RestClient restClient;

    public TossPaymentClient(final HttpComponentsClientHttpRequestFactory factory) {
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl(hostName)
                .build();
    }

    @Override
    public void postPayment(final PaymentRequest paymentRequest) {
        final String secret = "Basic " + Base64.getEncoder().encodeToString((secretKey + password).getBytes());

        restClient.post()
                .uri(paymentApi)
                .header(HttpHeaders.AUTHORIZATION, secret)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest);
    }
}
