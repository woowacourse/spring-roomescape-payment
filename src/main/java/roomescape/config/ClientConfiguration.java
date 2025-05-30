package roomescape.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfiguration {
    @Bean
    @Qualifier("tossClient")
    public RestClient tossClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.tosspayments.com")
                .requestFactory(getRequestFactory())
                .build();
    }

    public SimpleClientHttpRequestFactory getRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));
        return requestFactory;
    }
}
