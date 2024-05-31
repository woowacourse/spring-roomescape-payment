package roomescape.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient restClient(
            @Value("${client.connection-timeout-second}") int connectionTimeoutSecond,
            @Value("${client.read-timeout-second}") int readTimeoutSecond
    ) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(connectionTimeoutSecond))
                .setReadTimeout(Duration.ofSeconds(readTimeoutSecond))
                .build();

        return RestClient.builder(restTemplate)
                .build();
    }
}
