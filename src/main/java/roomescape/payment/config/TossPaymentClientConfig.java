package roomescape.payment.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossPaymentClient;

@Configuration
public class TossPaymentClientConfig {

    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String AUTH_TYPE_BASIC = "Basic";
    private static final String DELIMITER = ":";

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    @Value("${payment.connect-timeout-length}")
    private Duration connectTimeout;

    @Value("${payment.read-timeout-length}")
    private Duration readTimeout;

    @Bean
    public TossPaymentClient tossPaymentClient() {
        return new TossPaymentClient(createRestClient());
    }

    private RestClient createRestClient() {
        return RestClient.builder()
                .requestFactory(createRequestFactory())
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, createAuthorizationHeader())
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) connectTimeout.toMillis());
        factory.setReadTimeout((int) readTimeout.toMillis());
        return factory;
    }

    private String createAuthorizationHeader() {
        return AUTH_TYPE_BASIC + " " + base64Encode(secretKey + DELIMITER);
    }

    private String base64Encode(String input) {
        return Base64.getEncoder()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
