package roomescape.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import roomescape.fake.FakePaymentApiClient;
import roomescape.reservation.service.PaymentApiClient;

@TestConfiguration
public class PaymentApiClientConfig {

    @Bean
    @Primary
    public PaymentApiClient testPaymentService() {
        return new FakePaymentApiClient();
    }
}
