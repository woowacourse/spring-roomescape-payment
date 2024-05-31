package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.api.TossPaymentClient;
import roomescape.service.PaymentClient;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentClient getTossPaymentClient(@Value("${security.api.toss.secret-key}") String widgetSecretKey) {
        return new TossPaymentClient(RestClient.builder().baseUrl("https://api.tosspayments.com").build(), widgetSecretKey);
    }
}
