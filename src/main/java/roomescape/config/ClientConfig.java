package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.client.payment.PaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClient paymentClient(@Value("${payment.widget-secret-key}") String widgetSecretKey) {
        RestClient restClient = RestClient.builder().build();
        return new PaymentClient(widgetSecretKey, restClient);
    }
}
