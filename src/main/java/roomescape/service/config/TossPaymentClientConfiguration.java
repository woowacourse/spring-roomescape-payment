package roomescape.service.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TossPaymentClientConfiguration {

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final Duration CONNECT_TIMEOUT_DURATION = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT_DURATION = Duration.ofSeconds(30);

    @Bean
    public RestTemplate build(RestTemplateBuilder builder, TossPaymentConfigProperties properties) {
        return builder
            .defaultHeader(HttpHeaders.AUTHORIZATION, encode(properties.getTestSecretKey()))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .setConnectTimeout(CONNECT_TIMEOUT_DURATION)
            .setReadTimeout(READ_TIMEOUT_DURATION)
            .build();
    }

    private String encode(String key) {
        return AUTHORIZATION_PREFIX + new String(Base64.getEncoder().encode(key.getBytes(UTF_8)));
    }
}