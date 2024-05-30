package roomescape.global.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    private static final Duration CONNECT_TIME_OUT = Duration.ofSeconds(5);
    private static final Duration READ_TIME_OUT = Duration.ofSeconds(5);

    private final RestTemplateBuilder restTemplateBuilder;

    public RestClientConfig(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder(restTemplate())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder
                .setConnectTimeout(CONNECT_TIME_OUT)
                .setReadTimeout(READ_TIME_OUT)
                .build();
    }
}
