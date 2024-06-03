package roomescape.helper;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import roomescape.domain.payment.PaymentClient;
import roomescape.infrastructure.payment.FakePaymentClient;

@TestConfiguration
public class PaymentClientTestConfiguration {
    @Bean
    public PaymentClient paymentClient() {
        return new FakePaymentClient();
    }
}
