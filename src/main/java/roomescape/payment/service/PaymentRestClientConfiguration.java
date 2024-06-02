package roomescape.payment.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient.Builder;

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
    public PaymentService tossPaymentService(RestClientBuilders builders) {
        Builder builder = builders.getBuilder("toss");
        return new TossPaymentService(builder.build());
    }
}
