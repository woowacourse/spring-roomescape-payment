package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.PaymentService;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentService paymentClient(@Value("${payment.widget-secret-key}") String widgetSecretKey) {
        RestClient restClient = RestClient.builder().build();
        return new PaymentService(widgetSecretKey, restClient);
    }
}
