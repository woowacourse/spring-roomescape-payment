package roomescape.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("production")
public class RestClientConfiguration {
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(5);

    private final String authorizationToken;

    public RestClientConfiguration(@Value("${payment.toss.auth-token}") String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(createClientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory createClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(TIMEOUT_DURATION);
        factory.setConnectTimeout(TIMEOUT_DURATION);
        return factory;
    }
}
