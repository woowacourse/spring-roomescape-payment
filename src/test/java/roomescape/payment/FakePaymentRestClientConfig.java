package roomescape.payment;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class FakePaymentRestClientConfig {

    @Bean
    public TossPaymentClient tossPaymentClient() {
        return new FakeTossPaymentClient();
    }
}
