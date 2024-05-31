package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.TossPayRestClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private static final String TOSS_KEY_PREFIX = "Basic ";
    private static final long CONNECTION_TIMEOUT = 5L;
    private static final long READ_TIMEOUT = 30L;

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public TossPayRestClient tossPayRestClient() {
        return new TossPayRestClient(
                RestClient.builder()
                        .requestFactory(createHttpRequestFactory())
                        .defaultHeader(HttpHeaders.AUTHORIZATION, createTossAuthorizations())
                        .baseUrl("https://api.tosspayments.com")
                        .build()
        );
    }

    private ClientHttpRequestFactory createHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT))
                .withReadTimeout(Duration.ofSeconds(READ_TIMEOUT));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String createTossAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((paymentProperties.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return TOSS_KEY_PREFIX + new String(encodedBytes);
    }
}
