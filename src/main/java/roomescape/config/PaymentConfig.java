package roomescape.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentConfig {

    @Bean
    public RestClient restClient() {
        ClientHttpRequestFactorySettings httpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(5))
                .withReadTimeout(Duration.ofSeconds(5));

        return RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .requestFactory(ClientHttpRequestFactories.get(httpRequestFactorySettings))
                .build();
    }
}
