package roomescape.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.payment.domain.PaymentClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestClientConfiguration {

    @Bean
    public PaymentClient paymentClient() {
        return mock(PaymentClient.class);
    }
}
