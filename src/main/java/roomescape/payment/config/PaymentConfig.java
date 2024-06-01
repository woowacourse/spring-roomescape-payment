package roomescape.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public RestClientCustomizer paymentRestClientCustomizer() {
        return builder -> builder.baseUrl(paymentProperties.getBaseUrl())
                .defaultHeader("Authorization", paymentProperties.getEncodedSecretKey())
                .build();
    }
}
