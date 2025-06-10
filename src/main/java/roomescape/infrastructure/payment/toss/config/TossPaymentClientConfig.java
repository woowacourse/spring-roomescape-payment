package roomescape.infrastructure.payment.toss.config;

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
import roomescape.infrastructure.payment.toss.TossPaymentClient;

@Configuration
public class TossPaymentClientConfig {

    private static final String AUTH_TYPE_BASIC = "Basic";
    private static final String AUTH_DELIMITER = ":";
    private static final String URL_DELIMITER = "/";

    @Value("${payment.toss.base-url}")
    private String baseUrl;

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
                .baseUrl(String.join(URL_DELIMITER, baseUrl, "v1", "payments"))
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
        return AUTH_TYPE_BASIC + " " + base64Encode(secretKey + AUTH_DELIMITER);
    }

    private String base64Encode(String input) {
        return Base64.getEncoder()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
