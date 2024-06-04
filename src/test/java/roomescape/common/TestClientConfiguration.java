package roomescape.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.payment.domain.PaymentClient;

@TestConfiguration
public class TestClientConfiguration {

    @Bean
    public PaymentClient paymentClient() {
        return new StubPaymentClient();
    }
}
