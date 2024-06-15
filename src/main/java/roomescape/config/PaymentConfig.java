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
import roomescape.config.properties.PaymentClientProperties;
import roomescape.config.properties.PaymentClientProperty;

@Configuration
@EnableConfigurationProperties(PaymentClientProperties.class)
public class PaymentConfig {
    private final PaymentClientProperties properties;

    public PaymentConfig(PaymentClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PaymentClientProperty tossProperty() {
        return properties.getProperty("toss");
    }

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        PaymentClientProperty property = tossProperty();
        return restClientBuilder -> restClientBuilder
                .baseUrl(property.baseUrl())
                .requestFactory(createClientHttpRequestFactory(property));
    }

    @Bean
    public PaymentClient paymentClient(RestClient.Builder restClientBuilder) {
        RestClient restClient = new PaymentClientFactory(tossProperty())
                .createPaymentClient(restClientBuilder);
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build().createClient(PaymentClient.class);
    }

    private ClientHttpRequestFactory createClientHttpRequestFactory(PaymentClientProperty property) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(property.connectionTimeoutSeconds()))
                .withReadTimeout(Duration.ofSeconds(property.readTimeoutSeconds()));
        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }
}
