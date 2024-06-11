package roomescape.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.payment.toss.TossPaymentClient;

@Configuration
public class PaymentConfig {

    private final String secretKey;

    public PaymentConfig(@Value("${third-party-api.payment.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public PaymentClient paymentClient() {
        return new TossPaymentClient(secretKey);
    }
}
