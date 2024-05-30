package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentConfig {

    @Bean
    public RestClient tossPaymentRestClient() {
        return RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments/comfirm")
                        .build();
    }
}
