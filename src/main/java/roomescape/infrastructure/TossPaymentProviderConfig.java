package roomescape.infrastructure;

import static roomescape.infrastructure.EncodeUtil.encodeBase64;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TossPaymentProviderConfig {

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(final TossApiProperties properties) {
        return new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodeBase64(properties.widgetSecretKey))
            .rootUri(properties.baseUrl)
            .connectTimeout(Duration.ofMillis(properties.connectionTimeoutMillis))
            .readTimeout(Duration.ofMillis(properties.readTimeoutMillis));
    }

    @ConfigurationProperties(prefix = "api.toss")
    public record TossApiProperties (
        int connectionTryCount,
        long connectionTimeoutMillis,
        long readTimeoutMillis,
        String baseUrl,
        String confirmUri,
        String widgetSecretKey
    ) {

    }
}
