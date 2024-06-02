package roomescape.infra.payment;

import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.PaymentClient;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentClient implements PaymentClient {
    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final PaymentApiResponseErrorHandler errorHandler;
    private final TossPaymentProperties properties;
    private final RestClient restClient;

    public TossPaymentClient(PaymentApiResponseErrorHandler errorHandler, TossPaymentProperties properties) {
        this.errorHandler = errorHandler;
        this.properties = properties;
        this.restClient = RestClient.builder()
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(properties.url())
                .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        Duration readTimeout = Duration.ofMillis(properties.readTimeout());
        Duration connectTimeout = Duration.ofMillis(properties.connectTimeout());
        return ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(readTimeout)
                .withConnectTimeout(connectTimeout));
    }

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        String encodedSecretKey = Base64.getEncoder().encodeToString(properties.secretKey().getBytes());

        return Optional.ofNullable(restClient.post()
                        .uri("/v1/payments/confirm")
                        .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_PREFIX + encodedSecretKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(paymentRequest)
                        .retrieve()
                        .onStatus(errorHandler)
                        .body(PaymentResponse.class))
                .orElse(PaymentResponse.empty());
    }
}
