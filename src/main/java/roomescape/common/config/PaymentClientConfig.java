package roomescape.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.properties.PaymentClientProperties;
import roomescape.payment.exception.handler.PaymentApproveExceptionHandler;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {

    public static final int REQUEST_CONNECT_TIMEOUT_MINUTE = 3;
    public static final int REQUEST_READ_TIMEOUT_MINUTE = 5;
    private final PaymentClientProperties paymentClientProperties;

    public PaymentClientConfig(final PaymentClientProperties paymentClientProperties) {
        this.paymentClientProperties = paymentClientProperties;
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .requestFactory(generateTimeoutRequestFactory());
    }

    private SimpleClientHttpRequestFactory generateTimeoutRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMinutes(REQUEST_CONNECT_TIMEOUT_MINUTE));
        requestFactory.setReadTimeout(Duration.ofMinutes(REQUEST_READ_TIMEOUT_MINUTE));
        return requestFactory;
    }

    @Bean
    public RestClient paymentRestClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(paymentClientProperties.getBaseUrl())
                .build();
    }

    @Bean
    public PaymentApproveExceptionHandler paymentApproveExceptionHandler() {
        return new PaymentApproveExceptionHandler();
    }
}
