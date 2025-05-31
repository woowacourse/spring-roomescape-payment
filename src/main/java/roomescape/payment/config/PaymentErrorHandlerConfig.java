package roomescape.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.error.PaymentClientErrorHandler;
import roomescape.payment.error.PaymentServerErrorHandler;

@Configuration
public class PaymentErrorHandlerConfig {

    @Bean
    public ResponseErrorHandler paymentClientErrorHandler() {
        return new PaymentClientErrorHandler();
    }

    @Bean
    public ResponseErrorHandler paymentServerErrorHandler() {
        return new PaymentServerErrorHandler();
    }
}
