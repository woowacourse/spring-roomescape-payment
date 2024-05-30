package roomescape.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient restClient(
            @Value("${client.connection-timeout-second}") int connectionTimeoutSecond,
            @Value("${client.read-timeout-second}") int readTimeoutSecond
    ) {
        ClientHttpRequestFactory requestFactory = createRequestFactory(connectionTimeoutSecond, readTimeoutSecond);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    private ClientHttpRequestFactory createRequestFactory(int connectionTimeoutSecond, int readTimeoutSecond) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(connectionTimeoutSecond));
        factory.setReadTimeout(Duration.ofSeconds(readTimeoutSecond));

        return factory;
    }
}
