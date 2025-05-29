package roomescape.reservation;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.payment.application.PaymentService;

@TestConfiguration
public class ReservationTestConfig {

    @Bean
    @Primary
    public PaymentService dummyPaymentService() {
        return mock(PaymentService.class);
    }
}
