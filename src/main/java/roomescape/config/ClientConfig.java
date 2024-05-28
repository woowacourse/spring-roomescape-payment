package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public TossPaymentRestClient todoRestClient() {
        return new TossPaymentRestClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com/v1/payments/confirm").build()
        );
    }
}
