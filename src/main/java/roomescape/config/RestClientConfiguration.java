package roomescape.config;

import java.time.Duration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    private final PaymentConnectionProperties paymentConnectionProperties;

    public RestClientConfiguration(PaymentConnectionProperties paymentConnectionProperties) {
        this.paymentConnectionProperties = paymentConnectionProperties;
    }

    @Bean
    RestClient.Builder builder() {
        return RestClient.builder()
                .requestFactory(factory());
    }

    ClientHttpRequestFactory factory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(paymentConnectionProperties.getConnectionTimeout()))
                .withReadTimeout(Duration.ofSeconds(paymentConnectionProperties.getReadTimeout()));
        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory::new, settings);
    }
}
