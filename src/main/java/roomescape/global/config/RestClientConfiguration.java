package roomescape.global.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.TossPaymentClient;

@Configuration
public class RestClientConfiguration {

    private static final int CONNECTION_TIMEOUT_DURATION = 5;
    private static final int READ_TIMEOUT_DURATION = 45;

    @Bean
    public TossPaymentClient tossPaymentClient() {
        ClientHttpRequestFactory factory = getClientHttpRequestFactory();

        return new TossPaymentClient(
                RestClient.builder()
                        .requestFactory(factory)
                        .baseUrl("https://api.tosspayments.com")
                        .build()
        );
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_DURATION))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT_DURATION));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }
}
