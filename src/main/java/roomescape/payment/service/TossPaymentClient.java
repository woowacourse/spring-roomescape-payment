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
import roomescape.payment.dto.TossPaymentCancelResponse;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentErrorHandler;
import roomescape.reservation.dto.ReservationCancelReason;

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
                        .withReadTimeout(Duration.ofSeconds(30))))
                .build();
    }

    public TossPaymentResponse requestPayment(TossPaymentRequest tossPaymentRequest) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tossPaymentRequest)
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .body(TossPaymentResponse.class);
    }

    public TossPaymentCancelResponse requestPaymentCancel(String paymentKey, ReservationCancelReason cancelReason) {
        return restClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cancelReason)
                .retrieve()
                .onStatus(new PaymentErrorHandler())
                .body(TossPaymentCancelResponse.class);
    }

    private String createAuthorizations() {
        return KEY_PREFIX + new String(Base64.getEncoder().encode((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }
}
