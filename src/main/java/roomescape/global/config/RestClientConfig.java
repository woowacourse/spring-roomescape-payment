package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.global.restclient.PaymentWithRestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public PaymentWithRestClient paymentWithRestClient() {
        return new PaymentWithRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments")
                        .build()
        );
    }
}
