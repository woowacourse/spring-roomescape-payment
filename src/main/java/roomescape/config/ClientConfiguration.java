package roomescape.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.PaymentClient;
import roomescape.infrastructure.TossPaymentClient;

@Configuration
public class ClientConfiguration {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(3);

    @Bean
    public PaymentClient paymentClient() {
        return new TossPaymentClient(restClient());
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(clientFactory())
                .build();
    }

    private ClientHttpRequestFactory clientFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(CONNECT_TIMEOUT)
                .withReadTimeout(READ_TIMEOUT);
        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory::new, settings);
    }
}
