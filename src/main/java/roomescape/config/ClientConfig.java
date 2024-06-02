package roomescape.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentErrorHandler;
import roomescape.infra.PaymentClient;
import roomescape.infra.TossPaymentClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentErrorHandler paymentErrorHandler(ObjectMapper objectMapper) {
        return new PaymentErrorHandler(objectMapper);
    }

    @Bean
    public PaymentClient PaymentRestClient(PaymentErrorHandler paymentErrorHandler) {
        return new TossPaymentClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                        .defaultStatusHandler(paymentErrorHandler)
                        .build()
        );
    }
}
