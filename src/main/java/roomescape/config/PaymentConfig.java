package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.TossPayRestClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private static final String TOSS_KEY_PREFIX = "Basic ";

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public TossPayRestClient tossPayRestClient() {
        return new TossPayRestClient(
                RestClient.builder()
                        .defaultHeader(HttpHeaders.AUTHORIZATION, createTossAuthorizations())
                        .baseUrl("https://api.tosspayments.com")
                        .build()
        );
    }

    private String createTossAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((paymentProperties.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return TOSS_KEY_PREFIX + new String(encodedBytes);
    }
}
