package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.reservation.service.PaymentClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    public PaymentClient restClient() {
        return new PaymentClient(RestClient.builder().baseUrl("https://api.tosspayments.com").build());
    }
}
