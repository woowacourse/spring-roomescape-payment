package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.payment.TossPaymentClientErrorHandler;
import roomescape.service.PaymentService;

@Configuration
public class ClientConfig {
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    public ClientConfig(TossPaymentClientErrorHandler tossPaymentClientErrorHandler) {
        this.tossPaymentClientErrorHandler = tossPaymentClientErrorHandler;
    }

    @Bean
    public PaymentService paymentService() {
        return new PaymentService(
                RestClient.builder().baseUrl("https://api.tosspayments.com").build(),
                tossPaymentClientErrorHandler
        );
    }
}
