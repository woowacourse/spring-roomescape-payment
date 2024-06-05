package roomescape.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.infra.payment.PaymentApiLoggingInterceptor;
import roomescape.infra.payment.PaymentSecretKey;
import roomescape.infra.payment.TossPaymentProperties;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class PaymentClientConfig {
    private final TossPaymentProperties properties;

    public PaymentClientConfig(TossPaymentProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestInterceptor(new PaymentApiLoggingInterceptor())
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(properties.url())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        Duration readTimeout = Duration.ofMillis(properties.readTimeout());
        Duration connectTimeout = Duration.ofMillis(properties.connectTimeout());
        return ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(readTimeout)
                .withConnectTimeout(connectTimeout));
    }

    @Bean
    public PaymentSecretKey secretKey() {
        return new PaymentSecretKey(properties.secretKey());
    }
}
