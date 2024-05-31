package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.payment.infrastructure.FakePaymentGateway;
import roomescape.payment.infrastructure.PaymentGateway;

@TestConfiguration
public class TestPaymentGatewayConfig {

    @Bean
    @Primary
    public PaymentGateway paymentGateway() {
        return new FakePaymentGateway();
    }
}
