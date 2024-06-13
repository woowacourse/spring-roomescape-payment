package roomescape;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.domain.FakePayment;
import roomescape.domain.PaymentClient;

@TestConfiguration
public class TestPaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new FakePayment();
    }
}
