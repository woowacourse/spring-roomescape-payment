package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.TossPaymentErrorHandler;

@Component
public class TossPaymentClient {

    private static final String KEY_PREFIX = "Basic ";

    private final String tossSecretKey;
    private final RestClient restClient;

    public TossPaymentClient(@Value("${payment.secret-key}") String secretKey) {
        this.tossSecretKey = secretKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorizations())
                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(5))
                        .withReadTimeout(Duration.ofSeconds(5))))
                .build();
    }

    public void requestPayment(PaymentRequest paymentRequest) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new TossPaymentErrorHandler())
                .toBodilessEntity();
    }

    private String createAuthorizations() {
        return KEY_PREFIX + new String(Base64.getEncoder().encode((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }
}
