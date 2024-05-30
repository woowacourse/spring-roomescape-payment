package roomescape.application.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.application.payment.PaymentClient;
import roomescape.application.payment.PaymentErrorHandler;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentClientConfig {
    private final PaymentClientProperties properties;

    public PaymentClientConfig(PaymentClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClientCustomizer paymentRestClientCustomizer() {
        return builder -> builder
                .requestFactory(createPaymentClientRequestFactory())
                .defaultHeader(HttpHeaders.AUTHORIZATION, properties.getBasicKey())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(properties.getUrl())
                .defaultStatusHandler(new PaymentErrorHandler());
    }

    private ClientHttpRequestFactory createPaymentClientRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout(Duration.ofSeconds(30));

        return ClientHttpRequestFactories.get(
                JdkClientHttpRequestFactory.class, settings
        );
    }

    @Bean
    public PaymentClient tossPaymentClient(RestClient.Builder builder) {
        return new PaymentClient(builder.build());
    }
}
