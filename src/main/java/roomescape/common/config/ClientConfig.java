package roomescape.common.config;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Value("${payment.toss.base-url}")
    private String tossPaymentBaseUrl;

    @Value("${payment.toss.timeout-seconds}")
    private int tossPaymentTimeoutSeconds;

    @Bean
    public RestClient tossPaymentRestClient() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        final Duration timeout = Duration.ofSeconds(tossPaymentTimeoutSeconds);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        return RestClient.builder()
                .baseUrl(tossPaymentBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
