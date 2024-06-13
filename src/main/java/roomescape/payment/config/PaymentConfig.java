package roomescape.payment.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentErrorHandler;
import roomescape.payment.service.TossPaymentClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public TossPaymentClient tossPaymentClient(RestClient.Builder paymentRestClientBuilder) {
        return new TossPaymentClient(paymentRestClientBuilder);
    }

    @Bean
    public RestClientCustomizer paymentRestClientCustomizer() {
        return builder -> builder.baseUrl(paymentProperties.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, paymentProperties.getEncodedSecretKey())
                .defaultStatusHandler(new PaymentErrorHandler())
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(10))
                .withReadTimeout(Duration.ofSeconds(30));
        return ClientHttpRequestFactories.get(settings);
    }
}
