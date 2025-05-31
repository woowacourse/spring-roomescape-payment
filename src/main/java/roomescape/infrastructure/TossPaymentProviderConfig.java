package roomescape.infrastructure;

import static roomescape.infrastructure.EncodeUtil.base64Encode;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TossPaymentProviderConfig {

    private static final String TOSS_API_BASE_URL = "https://api.tosspayments.com";

    private static final String WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private static final String NO_PASSWORD_SIGN = ":";
    private static final String AUTHORIZATION_HEADER_VALUE = "Basic " + base64Encode(WIDGET_SECRET_KEY + NO_PASSWORD_SIGN);

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER_VALUE)
            .rootUri(TOSS_API_BASE_URL)
            .connectTimeout(Duration.ofSeconds(1))
            .readTimeout(Duration.ofSeconds(2));
    }
}
