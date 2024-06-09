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
    private static final String BASE_URL = "https://api.tosspayments.com/v1/payments";
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String SECRET_KEY_SUFFIX = ":";

    private final PaymentProperties paymentProperties;

    public PaymentRestClientConfiguration(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public PaymentClient paymentService() {
        return new PaymentClient(createRestClientBuilder(), initializeAuthorizationKey());
    }

    private RestClient createRestClientBuilder() {
        return RestClient.builder()
                .requestFactory(clientFactory())
                .baseUrl(BASE_URL)
                .build();
    }

    private ClientHttpRequestFactory clientFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(paymentProperties.getConnectionTimeout()))
                .withReadTimeout(Duration.ofSeconds(paymentProperties.getReadTimeout()));
        return ClientHttpRequestFactories.get(SimpleClientHttpRequestFactory::new, settings);
    }

    private String initializeAuthorizationKey() {
        Encoder encoder = Base64.getEncoder();
        String secretKey = paymentProperties.getSecretKey();
        byte[] encodedSecretKey = encoder.encode((secretKey + SECRET_KEY_SUFFIX).getBytes(StandardCharsets.UTF_8));
        return AUTHORIZATION_PREFIX + new String(encodedSecretKey);
    }
}
