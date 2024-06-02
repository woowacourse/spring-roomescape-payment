package roomescape.global.config;

import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import roomescape.global.exception.ClientExceptionHandler;
import roomescape.payment.TossPaymentClient;

@Configuration
public class RestClientConfiguration {

    private static final int CONNECTION_TIMEOUT_DURATION = 5;
    private static final int READ_TIMEOUT_DURATION = 45;
    private static final String HEADER_NAME = "Authorization";
    private static final String AUTHENTICATION_TYPE = "Basic";
    private static final String KEY_DELIMITER = ":";
    private static final ResponseErrorHandler HANDLER = new ClientExceptionHandler();

    @Value("${payment.secret-key}")
    private String widgetSecretKey;

    @Value("${payment.api.base}")
    private String apiBaseUrl;

    @Bean
    public TossPaymentClient tossPaymentClient() {
        ClientHttpRequestFactory factory = getClientHttpRequestFactory();

        return new TossPaymentClient(
                RestClient.builder()
                        .baseUrl(apiBaseUrl)
                        .defaultHeader(HEADER_NAME, provideHeaderValue())
                        .defaultStatusHandler(HANDLER)
                        .requestFactory(factory)
                        .build()
        );
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_DURATION))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_DURATION));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String provideHeaderValue() {
        String secretKey = widgetSecretKey + KEY_DELIMITER;
        return AUTHENTICATION_TYPE + " " + Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
}
