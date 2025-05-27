package roomescape.config.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;
import roomescape.utility.PaymentClient;
import roomescape.utility.TossPaymentClient;

@Configuration
@Profile("!test") // 테스트 프로필에서만 활성화
public class PaymentConfig {

    @Bean
    public PaymentClient paymentClient(
            @Value("${toss_payment_url}") String paymentUrl,
            @Value("${toss_payment_secret_key}") String secretKey
    ) {
        return new TossPaymentClient(
                RestClient.builder().baseUrl(paymentUrl).build(), secretKey);
    }
}
