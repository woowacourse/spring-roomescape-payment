package roomescape.payment.pg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
class TossRestClientConfiguration {
    private static final int CONNECTION_TIMEOUT_SECOND = 3;
    private static final int READ_TIMEOUT_SECOND = 30;

    private final String secretKey;
    private final String baseUrl;

    public TossRestClientConfiguration(@Value("${pg.toss.secret-key}") String secretKey,
                                       @Value("${pg.toss.base-url}") String baseUrl) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl;
    }

    @Bean
    public RestClient.Builder tossRestClientBuilder() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(AUTHORIZATION, encodeSecretKey(secretKey))
                .requestFactory(getRequestFactory());
    }

    private String encodeSecretKey(String secretKey) {
        return "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
    }

    private ClientHttpRequestFactory getRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECOND))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECOND));

        return ClientHttpRequestFactories.get(settings);
    }
}
