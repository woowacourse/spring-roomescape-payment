package roomescape.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfiguration {
    @Value("${toss.payment.base-url}")
    private String baseUrl;

    @Value("${toss.payment.connect-timeout}")
    private int connectTimeout;

    @Value("${toss.payment.read-timeout}")
    private int readTimeout;

    @Bean
    @Qualifier("tossClient")
    public RestClient tossClient(RestClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .requestFactory(getRequestFactory())
                .build();
    }

    public SimpleClientHttpRequestFactory getRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeout));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeout));
        return requestFactory;
    }
}
