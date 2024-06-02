package roomescape.config;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.client.PaymentClient;
import roomescape.client.PaymentClientFactory;
import roomescape.client.TossPaymentClientFactory;
import roomescape.config.properties.PaymentClientProperties;
import roomescape.config.properties.TossPaymentClientProperties;

@Configuration
@EnableConfigurationProperties(TossPaymentClientProperties.class)
public class PaymentConfig {
    private final PaymentClientProperties properties;

    public PaymentConfig(PaymentClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PaymentClientFactory paymentClientFactory() {
        return new TossPaymentClientFactory(properties);
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .baseUrl(properties.getBaseUrl())
                .requestFactory(createClientHttpRequestFactory());
    }

    @Bean
    public PaymentClient paymentClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = paymentClientFactory().createPaymentClient(restClientBuilder);
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build().createClient(PaymentClient.class);
    }

    private ClientHttpRequestFactory createClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(properties.getConnectionTimeoutSeconds()))
                .withReadTimeout(Duration.ofSeconds(properties.getReadTimeoutSeconds()));
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }
}
