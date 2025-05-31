package roomescape.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.error.PaymentClientErrorHandler;
import roomescape.payment.error.PaymentServerErrorHandler;

@Configuration
public class PaymentErrorHandlerConfig {

    @Bean
    public ErrorHandler paymentClientErrorHandler() {
        return new PaymentClientErrorHandler();
    }

    @Bean
    public ErrorHandler paymentServerErrorHandler() {
        return new PaymentServerErrorHandler();
    }
}
