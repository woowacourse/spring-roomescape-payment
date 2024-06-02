package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.service.FakePayment;
import roomescape.service.PaymentClient;

@TestConfiguration
public class TestPaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new FakePayment();
    }
}
