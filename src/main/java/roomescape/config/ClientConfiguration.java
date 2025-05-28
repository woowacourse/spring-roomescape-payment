package roomescape.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfiguration {
    @Bean
    public RestClient initializeRestClient(RestClient.Builder builder) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        return builder
                .baseUrl("https://api.tosspayments.com")
                //.requestFactory(requestFactory)
                .build();
    }
}
