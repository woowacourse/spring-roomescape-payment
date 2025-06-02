package roomescape.infrastructure;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TossPaymentConfig {

    private static final String TOSS_API_BASE_URL = "https://api.tosspayments.com";
    private static final String NO_PASSWORD_SIGN = ":";
    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final String widgetSecretKey;

    public TossPaymentConfig(@Value("${toss.widget-secret-key}") final String widgetSecretKey) {
        this.widgetSecretKey = widgetSecretKey;
    }

    @Bean
    public RestClient tossRestClient(final RestClient.Builder builder) {
        return builder.baseUrl(TOSS_API_BASE_URL).build();
    }

    @Bean
    public TossPaymentProvider tossPaymentProvider(final RestClient restClient) {
        return new TossPaymentProvider(restClient, createAuthorizationValue());
    }

    private String createAuthorizationValue() {
        return AUTHORIZATION_PREFIX + encodeSecretKey();
    }

    private String encodeSecretKey() {
        var base64Encoder = Base64.getEncoder();
        var secretKey = widgetSecretKey + NO_PASSWORD_SIGN;
        var encodedBytes = base64Encoder.encode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes);
    }
}
