package roomescape.infrastructure.payment;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientHttpRequestConfig {

    private final int connectTimeoutSeconds;
    private final int readTimeoutSeconds;

    public ClientHttpRequestConfig(@Value("${payment.connection-timeout-seconds}") int connectTimeoutSeconds,
                                   @Value("${payment.read-timeout-seconds}") int readTimeoutSeconds) {
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        factory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}

