package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class RestClientConfiguration {
    private static final int CONNECT_TIMEOUT = 20_000;
    private static final int CONNECT_REQUEST_TIMEOUT = 10_000;

    @Bean
    HttpComponentsClientHttpRequestFactory factory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setConnectionRequestTimeout(CONNECT_REQUEST_TIMEOUT);

        return factory;
    }
}
