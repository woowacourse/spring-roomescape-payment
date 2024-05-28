package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.service.PaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClient paymentClient() {
        RestClient restClient = RestClient.builder().build();
        return new PaymentClient(restClient);
    }
}
