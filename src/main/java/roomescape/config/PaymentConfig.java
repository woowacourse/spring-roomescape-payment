package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.reservation.client.TossRestClient;

@Configuration
public class PaymentConfig {

    @Bean
    @ConfigurationProperties(prefix = "third-party-api.toss-payment")
    public TossPaymentProperties tossPaymentProperties() {
        return new TossPaymentProperties();
    }

    @Bean
    public TossRestClient tossRestClient(RestTemplateBuilder builder, TossPaymentProperties paymentProperties) {
        return new TossRestClient(builder, paymentProperties);
    }
}
