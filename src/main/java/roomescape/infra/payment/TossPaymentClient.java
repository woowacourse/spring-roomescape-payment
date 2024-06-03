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
import org.springframework.web.client.RestClientException;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.payment.PaymentServerException;

@Component
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentClient implements PaymentClient {
    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final PaymentApiResponseErrorHandler errorHandler;
    private final TossPaymentProperties properties;
    private final RestClient restClient;
    private final String encodedSecretKey;

    public TossPaymentClient(PaymentApiResponseErrorHandler errorHandler, TossPaymentProperties properties) {
        this.errorHandler = errorHandler;
        this.properties = properties;
        this.restClient = getRestClient(properties);
        this.encodedSecretKey = getEncodedSecretKey(properties.secretKey());
    }

    private RestClient getRestClient(TossPaymentProperties properties) {
        return RestClient.builder()
                .requestInterceptor(new PaymentApiLoggingInterceptor())
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl(properties.url())
                .build();
    }

    private String getEncodedSecretKey(String secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        Duration readTimeout = Duration.ofMillis(properties.readTimeout());
        Duration connectTimeout = Duration.ofMillis(properties.connectTimeout());
        return ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(readTimeout)
                .withConnectTimeout(connectTimeout));
    }

    @Override
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        try {
            return Optional.ofNullable(restClient.post()
                            .uri("/v1/payments/confirm")
                            .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_PREFIX + encodedSecretKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(paymentRequest)
                            .retrieve()
                            .onStatus(errorHandler)
                            .body(PaymentResponse.class))
                    .orElse(PaymentResponse.empty());
        } catch (RestClientException e) {
            throw new PaymentServerException(e.getMessage(), e.getCause());
        }
    }
}
