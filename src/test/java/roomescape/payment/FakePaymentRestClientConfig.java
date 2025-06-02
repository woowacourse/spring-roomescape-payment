package roomescape.payment;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class FakePaymentRestClientConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new FakePaymentClient();
    }
}
