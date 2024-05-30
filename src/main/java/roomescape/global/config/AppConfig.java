package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.PaymentClient;
import roomescape.payment.PaymentWithRestClient;

@Configuration
public class AppConfig {

    @Bean
    public PaymentClient paymentWithRestClient() {
        return new PaymentWithRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments")
                        .build()
        );
    }
}
