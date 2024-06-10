package roomescape.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class ClientConfig {

    private final Duration connectionTimeoutSecond;
    private final Duration readTimeoutSecond;

    public ClientConfig(ClientProperties properties) {
        this.connectionTimeoutSecond = Duration.ofSeconds(properties.connectionTimeoutSecond());
        this.readTimeoutSecond = Duration.ofSeconds(properties.readTimeoutSecond());
    }

    @Bean
    public RestClient restClient() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(connectionTimeoutSecond)
                .setReadTimeout(readTimeoutSecond)
                .build();

        return RestClient.builder(restTemplate)
                .build();
    }
}
