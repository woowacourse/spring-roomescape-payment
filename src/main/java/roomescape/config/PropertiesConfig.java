package roomescape.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfig {

    @Bean
    @ConfigurationProperties(prefix = "third-party-api.toss-payment")
    public TossPaymentProperties tossPaymentProperties() {
        return new TossPaymentProperties();
    }
}
