package roomescape.controller.payment;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.infrastructure.payment.TossPaymentClient;
import roomescape.service.PaymentClient;

@TestConfiguration
public class TestPaymentConfiguration {

    @Bean
    @Primary
    public PaymentClient paymentClient() {
        return mock(TossPaymentClient.class);
    }
}
