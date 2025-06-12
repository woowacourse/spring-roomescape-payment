package roomescape.approval.infrastructure.toss.client;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestTossPaymentClientConfig {

    @Bean
    public TossPaymentClient tossPaymentClient() {
        return new FakeTossPaymentClient();
    }
}
