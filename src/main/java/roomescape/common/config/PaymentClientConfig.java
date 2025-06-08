package roomescape.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.exception.handler.PaymentApproveExceptionHandler;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    public static final int REQUEST_CONNECT_TIMEOUT_MILLISECOND = 3_000;
    public static final int REQUEST_READ_TIMEOUT_MILLISECOND = 5_000;
    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(final PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    @Profile("test")
    public RestClient restClientTest(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(paymentClientProperties.getBaseUrl())
                .build();
    }

    @Bean
    @Profile("!test")
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .requestFactory(generateRequestFactory())
                .baseUrl(paymentClientProperties.getBaseUrl())
                .build();
    }

    private SimpleClientHttpRequestFactory generateRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(REQUEST_CONNECT_TIMEOUT_MILLISECOND);
        requestFactory.setReadTimeout(REQUEST_READ_TIMEOUT_MILLISECOND);
        return requestFactory;
    }

    @Bean
    public PaymentApproveExceptionHandler paymentApproveExceptionHandler() {
        return new PaymentApproveExceptionHandler();
    }
}
