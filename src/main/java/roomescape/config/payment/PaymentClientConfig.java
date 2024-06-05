package roomescape.config.payment;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentClient;

@Configuration
public class PaymentClientConfig {
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String BASIC_DELIMITER = ":";
    private static final String AUTH_HEADER_PREFIX = "Basic ";

    @Value("${payment.secret-key}")
    private String secretKey;

    @Value("${payment.connect-timeout-length}")
    private Duration connectTimeoutLength;

    @Value("${payment.read-timeout-length}")
    private Duration readTimeoutLength;

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient(createRestClient());
    }

    private RestClient createRestClient() {
        return RestClient.builder()
                .requestFactory(createRequestFactory())
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorization())
                .requestInterceptor(new PaymentClientTimeoutInterceptor())
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory() {
        ClientHttpRequestFactorySettings requestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(connectTimeoutLength)
                .withReadTimeout(readTimeoutLength);
        return ClientHttpRequestFactories.get(requestFactorySettings);
    }

    private String createAuthorization() {
        return AUTH_HEADER_PREFIX + encodeToBase64(secretKey + BASIC_DELIMITER);
    }

    private String encodeToBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
