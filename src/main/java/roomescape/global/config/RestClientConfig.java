package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;
import roomescape.global.restclient.PaymentWithRestClient;

@Controller
public class RestClientConfig {

    @Bean
    public PaymentWithRestClient paymentRestClient() {
        return new PaymentWithRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments")
                        .build()
        );
    }
}
