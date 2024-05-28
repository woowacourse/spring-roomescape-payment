package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@Configuration
public class RestClientConfiguration {

    private static final int CONNECT_TIMEOUT = 10_000;

    @Bean
    public JdkClientHttpRequestFactory httpRequestFactoryConfig() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(CONNECT_TIMEOUT);
        return factory;
    }
}
