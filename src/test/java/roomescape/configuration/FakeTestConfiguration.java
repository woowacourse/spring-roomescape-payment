package roomescape.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.infrastructure.PaymentClient;

@TestConfiguration
public class FakeTestConfiguration {

    @Primary
    @Bean
    public PaymentClient fakePaymentClient() {
        return new FakePaymentClient();
    }
}
