package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.infrastructure.PaymentClient;
import roomescape.utils.FakePaymentClient;

@TestConfiguration
public class TestConfig {
    @Bean
    public PaymentClient paymentClient() {
        return new FakePaymentClient();
    }
}
