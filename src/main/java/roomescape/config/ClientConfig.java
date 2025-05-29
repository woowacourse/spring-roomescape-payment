package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.TossPaymentService;

@Configuration
public class ClientConfig {
    @Bean
    public TossPaymentService getReservationPaymentClient() {
        return new TossPaymentService(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments/").build()
        );
    }
}
