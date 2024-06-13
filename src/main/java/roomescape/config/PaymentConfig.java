package roomescape.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.reservation.client.TossRestClient;

@Configuration
public class PaymentConfig {

    @Bean
    public TossRestClient tossRestClient(RestTemplateBuilder builder, TossPaymentProperties paymentProperties) {
        return new TossRestClient(builder, paymentProperties);
    }
}
