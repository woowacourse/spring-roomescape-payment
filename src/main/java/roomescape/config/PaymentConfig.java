package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.TossPayRestClient;

@Configuration
public class PaymentConfig {

    @Value("${payment.secret-key}")
    private String paymentSecretKey;

    @Bean
    public TossPayRestClient tossPayRestClient() {
        return new TossPayRestClient(paymentSecretKey, RestClient.builder()
                .baseUrl("https://api.tosspayments.com")
                .build());
    }
}
