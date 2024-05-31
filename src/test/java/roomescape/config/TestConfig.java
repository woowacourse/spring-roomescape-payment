package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.domain.payment.pg.FakePaymentGateway;
import roomescape.domain.payment.pg.PaymentGateway;

@TestConfiguration
public class TestConfig {

    @Primary
    @Bean
    public PaymentGateway paymentGateway() {
        return new FakePaymentGateway();
    }
}
