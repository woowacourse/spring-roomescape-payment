package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.service.payment.PaymentRestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments").build(),
                new ObjectMapper()
        );
    }
}
