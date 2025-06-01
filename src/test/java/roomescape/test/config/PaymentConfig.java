package roomescape.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import roomescape.test.stub.PaymentClientStub;
import roomescape.utility.payment.PaymentClient;

@Configuration
@Profile("test") // 테스트 프로필에서만 활성화
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClientStub();
    }
}
