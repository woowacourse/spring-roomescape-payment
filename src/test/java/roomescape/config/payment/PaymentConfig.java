package roomescape.config.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import roomescape.utility.PaymentClient;
import roomescape.utility.PaymentClientStub;

@Configuration
@Profile("test")
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClientStub();
    }
}
