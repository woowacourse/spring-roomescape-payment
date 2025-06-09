package roomescape.reservation;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.payment.infrastructure.TossPaymentClient;

@TestConfiguration
public class ReservationTestConfig {

    @Bean
    @Primary
    public TossPaymentClient dummyTossPaymentClient() {
        return mock(TossPaymentClient.class);
    }
}
