package roomescape.payment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient.Builder;
import roomescape.payment.domain.PaymentProperties;
import roomescape.payment.domain.RestClientBuilders;
import roomescape.payment.service.PaymentClient;
import roomescape.payment.service.TossPaymentClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentRestClientConfiguration {
    private final PaymentProperties paymentProperties;

    public PaymentRestClientConfiguration(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public RestClientBuilders builders() {
        return new RestClientBuilders(paymentProperties);
    }

    @Bean
    public PaymentClient tossPaymentService(RestClientBuilders builders) {
        Builder builder = builders.getBuilder("toss");
        return new TossPaymentClient(builder.build());
    }
}
