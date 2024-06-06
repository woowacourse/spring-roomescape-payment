package roomescape.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import roomescape.infrastructure.PaymentApproveErrorHandler;
import roomescape.infrastructure.PaymentRefundErrorHandler;
import roomescape.infrastructure.TossPaymentsProperties;

@Configuration
public class TossPaymentClientConfig {

    private final TossPaymentsProperties tossPaymentsProperties;

    public TossPaymentClientConfig(TossPaymentsProperties tossPaymentsProperties) {
        this.tossPaymentsProperties = tossPaymentsProperties;
    }

    @Bean
    public RestTemplate paymentApproveRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(tossPaymentsProperties.baseUrl())
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .errorHandler(new PaymentApproveErrorHandler())
                .build();
    }

    @Bean
    public RestTemplate paymentRefundRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(tossPaymentsProperties.baseUrl())
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .errorHandler(new PaymentRefundErrorHandler())
                .build();
    }
}
