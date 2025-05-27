package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClientService;

public class ClientConfig {
    @Bean
    public PaymentClientService paymentClient() {
        return new PaymentClientService(
                RestClient.builder().baseUrl("https://api.tosspayments.com").build()
        );
    }
}
