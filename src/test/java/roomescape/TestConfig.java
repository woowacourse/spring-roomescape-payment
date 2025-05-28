package roomescape;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.payment.StubPaymentClient;
import roomescape.payment.application.service.PaymentClient;

@TestConfiguration
public class TestConfig {

    @Bean("testPaymentClient")
    @Primary
    public PaymentClient paymentClient() {
        return new StubPaymentClient();
    }
}
