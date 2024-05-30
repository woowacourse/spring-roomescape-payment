package roomescape.global.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    private static final int TIMEOUT_SECONDS = 5;
    @Bean
    public ClientHttpRequestFactory getRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .withReadTimeout(Duration.ofSeconds(TIMEOUT_SECONDS));
        return ClientHttpRequestFactories.get(settings);
    }
}
