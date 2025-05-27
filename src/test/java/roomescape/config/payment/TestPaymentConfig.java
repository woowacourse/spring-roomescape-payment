package roomescape.config.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import roomescape.utility.PayClientStub;
import roomescape.utility.PaymentClient;

@Configuration
@Profile("test") // 테스트 프로필에서만 활성화
public class TestPaymentConfig {

    @Bean
    public PaymentClient paymentClient() {
        return new PayClientStub();
    }
}
