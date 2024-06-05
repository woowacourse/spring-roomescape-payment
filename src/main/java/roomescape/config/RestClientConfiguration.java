package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.client.PaymentClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class RestClientConfiguration {

    private static final String AUTHORIZATION_PREFIX = "Basic ";

    @Value("${client.payment.secret-key}")
    private String SECRET_KEY;

    @Bean
    public PaymentClient restClient() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = AUTHORIZATION_PREFIX + new String(encodedBytes);

        RestClient restClient = RestClient.builder()
                .requestFactory(getRequestFactoryWithTimeout())
                .defaultHeader("Authorization", authorizations)
                .build();
        return new PaymentClient(restClient);
    }

    private ClientHttpRequestFactory getRequestFactoryWithTimeout() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMinutes(2))
                .withReadTimeout(Duration.ofMinutes(2));
        return ClientHttpRequestFactories.get(settings);
    }
}
