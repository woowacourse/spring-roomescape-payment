package roomescape.application.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient.Builder;
import roomescape.application.payment.PaymentClient;
import roomescape.infrastructure.payment.TossPaymentClient;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {
    private final PaymentClientProperties properties;

    public PaymentClientConfig(PaymentClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PaymentRestClientBuilders builders() {
        return new PaymentRestClientBuilders(properties);
    }

    @Bean
    public PaymentClient tossPaymentClient() {
        Builder builder = builders().get("toss")
                .defaultStatusHandler(new PaymentErrorHandler());
        return new TossPaymentClient(builder.build());
    }
}
