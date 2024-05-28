package roomescape.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import roomescape.dto.PaymentRequest;

@Component
public class PaymentClient {

    private final String secretKey;
    private final RestClient restClient;

    public PaymentClient(@Value("${security.payment.secret-key}") String secretKey, @Value("${security.payment.url}") String url) {
        this.secretKey = secretKey;
        this.restClient = RestClient.builder().baseUrl(url).build();
    }

    public void approve(PaymentRequest paymentRequest) {
        Base64.Encoder encoder = Base64.getEncoder();
        String basic = encoder.encodeToString((secretKey + ":").getBytes());
        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", "Basic " + basic)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentClientResponseErrorHandler())
                .toBodilessEntity();
    }

}
