package roomescape.infrastructure;

import static roomescape.infrastructure.EncodeUtil.encodeBase64;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public TossApiProperties tossApiProperties(
        @Value("${api.toss.connection-try-count}") final int connectionTryCount,
        @Value("${api.toss.connection-timeout-millis}") final long connectionTimeoutMillis,
        @Value("${api.toss.read-timeout-millis}") final long readTimeoutMillis,
        @Value("${api.toss.base-url}") final String baseUrl,
        @Value("${api.toss.confirm-uri}") final String confirmUri,
        @Value("${api.toss.widget-secret-key}") final String widgetSecretKey
    ) {
        return new TossApiProperties(connectionTryCount, connectionTimeoutMillis, readTimeoutMillis,
            baseUrl, confirmUri, widgetSecretKey);
    }

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
