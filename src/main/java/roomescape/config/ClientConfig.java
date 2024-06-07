package roomescape.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.payment.TimeoutInterceptor;

import java.time.Duration;

@Configuration
public class ClientConfig {

    private static final int CONNECT_TIMEOUT_SECONDS = 5;
    private static final int READ_TIMEOUT_SECONDS = 30;

    @Bean
    public RestClient.Builder restClientBuilder() { // todo
        return RestClient.builder()
                .requestFactory(getRequestFactory())
                .requestInterceptor(new TimeoutInterceptor());
    }

    private ClientHttpRequestFactory getRequestFactory() {
        return ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                        .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
        );
    }
}
