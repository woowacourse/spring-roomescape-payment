package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentRestClientConfiguration {
    private final PaymentProperties properties;

    public PaymentRestClientConfiguration(PaymentProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient(createRestClientBuilder(), initializeAuthorizationKey());
    }

    private RestClient createRestClientBuilder() {
        return RestClient.builder()
                .requestFactory(clientFactory())
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    private ClientHttpRequestFactory clientFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(properties.getConnectionTimeout()))
                .withReadTimeout(Duration.ofSeconds(properties.getReadTimeout()));
        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory::new, settings);
    }

    private String initializeAuthorizationKey() {
        Encoder encoder = Base64.getEncoder();
        byte[] encodedSecretKey = encoder.encode(properties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        return properties.getAuthorizationPrefix() + " " + new String(encodedSecretKey);
    }
}
