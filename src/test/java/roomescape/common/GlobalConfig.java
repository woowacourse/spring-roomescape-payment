package roomescape.common;

import java.time.Clock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.fake.FakeTossPaymentService;
import roomescape.reservation.external.toss.TossPaymentService;

@TestConfiguration
public class GlobalConfig {

    @Bean
    @Primary
    public Clock testClock() {
        return Constant.FIXED_CLOCK;
    }

    @Bean
    @Primary
    public TossPaymentService testPaymentService() {
        return new FakeTossPaymentService();
    }
}
