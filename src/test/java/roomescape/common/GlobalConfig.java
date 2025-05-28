package roomescape.common;

import java.time.Clock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.fake.FakePaymentApiClient;
import roomescape.reservation.service.PaymentApiClient;

@TestConfiguration
public class GlobalConfig {

    @Bean
    @Primary
    public Clock testClock() {
        return Constant.FIXED_CLOCK;
    }

    @Bean
    @Primary
    public PaymentApiClient testPaymentService() {
        return new FakePaymentApiClient();
    }
}
