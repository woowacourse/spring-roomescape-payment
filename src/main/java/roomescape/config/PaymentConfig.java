package roomescape.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import roomescape.exception.TossPaymentExceptionHandler;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.PaymentClientBuliders;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {
    private final PaymentProperties paymentProperties;

    public PaymentConfig(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    @Bean
    public PaymentClient tossPaymentClient() {
        RestClient toss = builders().get("toss")
                .defaultStatusHandler(responseErrorHandler())
                .build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(toss))
                .build().createClient(PaymentClient.class);
    }

    @Bean
    public PaymentClientBuliders builders() {
        return new PaymentClientBuliders(createBuilders());
    }

    private Map<String, RestClient.Builder> createBuilders() {
        Map<String, RestClient.Builder> builders = new HashMap<>();
        paymentProperties.getProperties().keySet()
                .forEach(name -> builders.put(name, createBuilder(name)));
        return builders;
    }

    private ResponseErrorHandler responseErrorHandler() {
        return new TossPaymentExceptionHandler();
    }

    private Builder createBuilder(String name) {
        PaymentProperty property = paymentProperties.get(name);

        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory(property))
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorization(property))
                .baseUrl(property.baseUrl());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(PaymentProperty paymentProperty) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(paymentProperty.connectionTime()))
                .withReadTimeout(Duration.ofSeconds(paymentProperty.readTime()));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }

    private String authorization(PaymentProperty paymentProperty) {
        byte[] encodedBytes = Base64.getEncoder().encode(
                (paymentProperty.secretKey() + ":").getBytes(StandardCharsets.UTF_8)
        );
        return "Basic " + new String(encodedBytes);
    }
}
