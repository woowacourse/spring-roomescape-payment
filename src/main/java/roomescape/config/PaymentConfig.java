package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentService;
import roomescape.service.TossPaymentService;

@Configuration
public class PaymentConfig {
    @Bean
    public PaymentService restClient() {
        return new TossPaymentService(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments/confirm").build());
    }
}
