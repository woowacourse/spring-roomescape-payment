package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private static final int CONNECT_TIME_OUT = 2 * 1000;

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    }
}
