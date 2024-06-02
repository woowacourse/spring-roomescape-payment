package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentErrorHandler;
import roomescape.infra.PaymentClient;
import roomescape.infra.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentErrorHandler paymentErrorHandler() {
        return new PaymentErrorHandler();
    }

    @Bean
    public PaymentClient tossPaymentRestClient() {
        return new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                        .build(),
                paymentErrorHandler()
        );
    }
}
