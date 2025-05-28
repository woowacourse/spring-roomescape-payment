package roomescape.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.infrastructure.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new TossPaymentClient(
                RestClient.builder().build()
        );
    }
}
