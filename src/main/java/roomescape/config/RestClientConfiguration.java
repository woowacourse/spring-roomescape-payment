package roomescape.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;

@Configuration
public class RestClientConfiguration {
    private static final int CONNECT_TIMEOUT = 3;
    private static final int READ_TIMEOUT = 40;

    @Bean
    ClientHttpRequestFactory factory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT));
        return ClientHttpRequestFactories.get(settings);
    }
}
