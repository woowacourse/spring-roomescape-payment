package roomescape.global.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class RestClientConfig {
    @Bean
    public ClientHttpRequestFactory getRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofMinutes(5));
        return ClientHttpRequestFactories.get(settings);
    }
}
