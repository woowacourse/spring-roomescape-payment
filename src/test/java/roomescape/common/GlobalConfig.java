package roomescape.common;

import java.time.Clock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.fake.FakePaymentService;
import roomescape.fake.FakePaymentServiceDecider;
import roomescape.reservation.service.PaymentService;
import roomescape.reservation.service.PaymentServiceDecider;
import roomescape.reservation.service.dto.PaymentRequest;

@TestConfiguration
public class GlobalConfig {

    @Bean
    @Primary
    public Clock testClock() {
        return Constant.FIXED_CLOCK;
    }

    @Bean
    @Primary
    public PaymentServiceDecider paymentServiceDecider() {
        return new FakePaymentServiceDecider();
    }

    @Bean
    @Primary
    public PaymentService<PaymentRequest> paymentService() {
        return new FakePaymentService();
    }
}
