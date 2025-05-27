package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.reservation.PaymentRestClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments")
                        .build()
        );
    }
}
