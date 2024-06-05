package roomescape.config;

import java.time.Duration;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentConfig {

    private static final String TOSS_PAYMENTS_BASE_URI = "https://api.tosspayments.com/v1/payments";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    @Bean
    public RestClient tossPaymentRestClient() {
        return RestClient.builder()
                .baseUrl(TOSS_PAYMENTS_BASE_URI)
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(CONNECT_TIMEOUT)
                .withReadTimeout(READ_TIMEOUT);
        return ClientHttpRequestFactories.get(settings);
    }
}
