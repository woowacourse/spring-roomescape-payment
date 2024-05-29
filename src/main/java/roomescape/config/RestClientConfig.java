package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentClient;

@Configuration
public class RestClientConfig {
    @Bean
    public PaymentClient paymentRestClient() {
        return new PaymentClient(
                RestClient.builder().build()
        );
    }
}
