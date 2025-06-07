package roomescape.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.exception.handler.PaymentApproveExceptionHandler;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    public static final int REQUEST_TIMEOUT_TIME = 3_000;
    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(final PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .requestFactory(generateRequestFactory())
                .baseUrl(paymentClientProperties.getBaseUrl())
                .build();
    }

    private HttpComponentsClientHttpRequestFactory generateRequestFactory() {
        HttpComponentsClientHttpRequestFactory  requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(REQUEST_TIMEOUT_TIME);
        return requestFactory;
    }

    @Bean
    public PaymentApproveExceptionHandler paymentApproveExceptionHandler() {
        return new PaymentApproveExceptionHandler();
    }
}
