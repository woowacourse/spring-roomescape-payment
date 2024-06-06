package roomescape;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.domain.payment.PaymentClient;
import roomescape.support.FakePaymentClient;

@TestConfiguration
public class TestConfig {

    @Primary
    @Bean
    public PaymentClient fakePaymentClient() {
        return new FakePaymentClient();
    }
}
