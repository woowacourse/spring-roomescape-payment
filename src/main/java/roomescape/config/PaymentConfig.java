package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.api.TossPaymentClient;
import roomescape.service.PaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient getTossPaymentClient() {
        return new TossPaymentClient(RestClient.builder().baseUrl("https://api.tosspayments.com").build());
    }
}
