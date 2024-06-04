package roomescape.payment;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {
    @Bean
    public TossPaymentClient tossPaymentClient(PaymentProperties paymentProperties) {
        return new TossPaymentClient(restClient(paymentProperties));
    }

    @Bean
    public RestClient restClient(PaymentProperties paymentProperties) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(30L))
                .withConnectTimeout(Duration.ofSeconds(3L));
        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        return RestClient.builder().baseUrl("https://api.tosspayments.com")
                .defaultHeader("Authorization", getAuthorizations(paymentProperties.getSecretKey()))
                .requestFactory(requestFactory)
                .build();
    }

    private String getAuthorizations(String secretKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
