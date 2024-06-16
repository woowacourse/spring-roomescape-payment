package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import roomescape.domain.payment.PaymentClient;
import roomescape.support.mock.FakePayment;

@Configuration
public class TestPaymentConfig {

    @Primary
    @Bean
    public PaymentClient paymentClient() {
        return new FakePayment();
    }
}
