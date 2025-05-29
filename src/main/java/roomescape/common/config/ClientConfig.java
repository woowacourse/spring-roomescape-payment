package roomescape.common.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private static final String TOSS_PAYMENT_BASE_URL = "https://api.tosspayments.com/";
    private static final Duration TOSS_PAYMENT_TIMEOUT = Duration.ofSeconds(3);

    @Bean
    public RestClient tossPaymentRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TOSS_PAYMENT_TIMEOUT);
        requestFactory.setReadTimeout(TOSS_PAYMENT_TIMEOUT);

        return RestClient.builder()
                .baseUrl(TOSS_PAYMENT_BASE_URL)
                .requestFactory(requestFactory)
                .build();
    }
}
