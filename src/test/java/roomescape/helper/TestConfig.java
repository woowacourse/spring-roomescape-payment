package roomescape.helper;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.reservation.service.PaymentService;

@TestConfiguration
public class TestConfig {

    @Bean
    public PaymentService paymentService() {
        return Mockito.mock(PaymentService.class);
    }
}
