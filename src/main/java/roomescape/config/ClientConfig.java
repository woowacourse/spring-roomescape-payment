package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentService;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentService paymentService() {
        return new PaymentService(RestClient.builder().baseUrl("https://api.tosspayments.com").build());
    }
}
