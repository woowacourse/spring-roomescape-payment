package roomescape.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.infrastructure.TossPaymentsProperties;

@Configuration
public class TossPaymentRestClientConfig {

    private final TossPaymentsProperties tossPaymentsProperties;

    public TossPaymentRestClientConfig(TossPaymentsProperties tossPaymentsProperties) {
        this.tossPaymentsProperties = tossPaymentsProperties;
    }

    @Bean
    public RestClient getTossPaymentRestClient() {
        return RestClient.builder()
                .baseUrl(tossPaymentsProperties.getBaseUrl())
                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(5))
                        .withReadTimeout(Duration.ofSeconds(30))))
                .build();
    }
}
