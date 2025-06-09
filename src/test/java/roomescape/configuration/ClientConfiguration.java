package roomescape.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import roomescape.cllient.payment.PaymentClient;
import roomescape.test.stub.PaymentClientStub;

@Configuration
@Profile("test")
public class ClientConfiguration {

    @Bean
    public PaymentClient tossPaymentclient() {
        return new PaymentClientStub();
    }
}
