package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfigProperties {

    @Bean
    @ConfigurationProperties(prefix = "payment.toss")
    public TossPaymentConfigProperties getTossProperties() {
        return new TossPaymentConfigProperties();
    }
}
