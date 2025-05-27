package roomescape.config.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.utility.PayClientStub;
import roomescape.utility.PaymentClient;

@Configuration
public class TestPaymentConfig {

    @Bean
    public PaymentClient tossPaymentClient() {
        return new PayClientStub();
    }
}
