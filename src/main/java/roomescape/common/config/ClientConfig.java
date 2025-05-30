package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${payment.toss.base-url}")
    private String tossPaymentBaseUrl;

    @Value("${payment.toss.timeout-seconds}")
    private int tossPaymentTimeoutSeconds;

    @Value("${payment.toss.secret-key}")
    private String tossSecretKey;

    @Bean(name = "tossPaymentRestClient")
    public RestClient tossPaymentRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        final Duration timeout = Duration.ofSeconds(tossPaymentTimeoutSeconds);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        final String encodedKey = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl(tossPaymentBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .requestFactory(requestFactory)
                .build();
    }
}
