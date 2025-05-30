package roomescape.common.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.infrastructure.TossPaymentProperties;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
@RequiredArgsConstructor
public class ClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

    @Bean(name = "tossPaymentRestClient")
    public RestClient tossPaymentRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        final Duration timeout = Duration.ofSeconds(tossPaymentProperties.getTossPaymentTimeoutSeconds());
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        final String encodedKey = Base64.getEncoder()
                .encodeToString((tossPaymentProperties.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        return RestClient.builder()
                .baseUrl(tossPaymentProperties.getTossPaymentBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .requestFactory(requestFactory)
                .build();
    }
}
