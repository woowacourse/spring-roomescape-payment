package roomescape.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

@Configuration
public class RestClientConfig {

    @Value("${timeout.connection}")
    private int TIMEOUT_CONNECTION;
    @Value("${timeout.read}")
    private int TIMEOUT_READ;

    @Bean
    public Builder restClientBuilder() {
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_CONNECTION);
        factory.setReadTimeout(TIMEOUT_READ);
        return RestClient.builder().requestFactory(factory);
    }
}
