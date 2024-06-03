package roomescape.infrastructure.payment;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@EnableConfigurationProperties(TossPaymentClientProperties.class)
public class TossPaymentClient implements PaymentClient {
    private final RestClient restClient;
    private final TossPaymentClientProperties properties;

    public TossPaymentClient(TossPaymentClientProperties properties) {
        this.properties = properties;
        this.restClient = getRestClient();
    }

    public RestClient getRestClient() {
        return RestClient.builder().baseUrl(properties.baseUrl()).build();
    }

    @Override
    public Payment approve(PaymentRequest request) {
        String authorizations = getEncodedKey();
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .body(request)
                .retrieve()
                .body(Payment.class);
    }

    private String getEncodedKey() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((properties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
