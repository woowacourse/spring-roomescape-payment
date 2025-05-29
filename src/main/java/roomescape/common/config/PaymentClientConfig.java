package roomescape.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.exception.handler.PaymentApproveExceptionHandler;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(final PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(paymentClientProperties.getBaseUrl())
                .build();
    }

    @Bean
    PaymentApproveExceptionHandler paymentApproveExceptionHandler() {
        return new PaymentApproveExceptionHandler();
    }
}
