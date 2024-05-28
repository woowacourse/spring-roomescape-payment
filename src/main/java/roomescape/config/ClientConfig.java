package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClient paymentRestClient() {
        return new PaymentClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com").build()
        );
    }
}
