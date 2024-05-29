package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.service.DummyPaymentClient;
import roomescape.service.payment.PaymentClient;

@TestConfiguration
public class TestConfig {
    @Bean
    public PaymentClient paymentClient() {
        return new DummyPaymentClient();
    }
}
