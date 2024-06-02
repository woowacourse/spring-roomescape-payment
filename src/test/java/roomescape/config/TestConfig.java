package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.service.PaymentClient;
import roomescape.support.FakePaymentClient;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Clock testClock() {
        Instant fixedInstant = Instant.parse("2024-04-08T00:00:00Z");
        ZoneId zone = ZoneOffset.UTC;

        return Clock.fixed(fixedInstant, zone);
    }

    @Bean
    @Primary
    public PaymentClient fakePaymentClient() {
        return new FakePaymentClient();
    }
}
