package roomescape.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.exception.PaymentClientErrorHandler;
import roomescape.exception.PaymentServerErrorHandler;
import roomescape.infra.PaymentRestClient;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentClientErrorHandler paymentClientErrorHandler() {
        return new PaymentClientErrorHandler();
    }

    @Bean
    public PaymentServerErrorHandler paymentServerErrorHandler() {
        return new PaymentServerErrorHandler();
    }

    @Bean
    public PaymentRestClient paymentRestClient() {
        return new PaymentRestClient(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                        .build(),
                paymentClientErrorHandler(),
                paymentServerErrorHandler()
        );
    }
}
